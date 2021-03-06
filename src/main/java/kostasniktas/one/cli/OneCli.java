package kostasniktas.one.cli;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.vm.VirtualMachine;
import org.opennebula.client.vm.VirtualMachinePool;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class OneCli {

    private static final Pattern PATTERN_IP = Pattern.compile(".*\\[(\\d+\\.\\d+\\.\\d+\\.\\d+)\\].*");

    private static String getIPFromXML(String xml) {
        int in = xml.indexOf("<IP>");
        int in2 = xml.indexOf("</IP>");
        String ipxml = xml.substring(in, in2);

        Matcher m = PATTERN_IP.matcher(ipxml);
        if (m.matches()) {
            return m.group(1);
        }
        return null;
    }

    private static CommandLine parseArguments (String... args) throws ParseException {
        Options options = new Options();

        Option optionHelp = new Option(null, "help", false, "Print help information");
        options.addOption(optionHelp);

        //TODO: When we actually search for more things, it would probably require a refactor for checking
        //       what to search on.
        OptionGroup optionGroupSearch = new OptionGroup();
        optionGroupSearch.addOption(new Option(null, "search-name", true, "Search for ONE nodes by name"));
        options.addOptionGroup(optionGroupSearch);

        Option optionView = new Option("v", "view", true, "The information to display for information found"); //TODO: List of info
        options.addOption(optionView);

        CommandLineParser parser = new DefaultParser();

        CommandLine commandLine = parser.parse(options, args);

        if (commandLine.hasOption("help")) {
            HelpFormatter helpFormatter = new HelpFormatter();
            helpFormatter.printHelp("one-cli", options, true);
            System.exit(0);
        }

        return commandLine;
    }

    public static void main (String... args) {
        String searchName = "NOT_A_SELECTABLE_NODE_I_HOPE";
        String[] views = { "name", "ip" };


        CommandLine commandLine = null;
        try {
            commandLine = parseArguments(args);
        } catch (ParseException exp) {
            System.err.println("Parsing failed. Reason: " + exp.getMessage());
            System.exit(1);
        }

        if (System.getenv("ONE_AUTH") == null || System.getenv("ONE_XMLRPC") == null) {
            //TODO: usage
            System.err.println("Env var ONE_AUTH must be a file location and ONE_XMLRPC must be the RPC URL");
            System.exit(1);
        }

        if (commandLine.hasOption("search-name")) {
            searchName = commandLine.getOptionValue("search-name");
        }

        if (commandLine.hasOption("view")) {
            views = commandLine.getOptionValue("view").split(",");
        }

        Client oneClient;
        try {
            oneClient = new Client();

            VirtualMachinePool pool = new VirtualMachinePool(oneClient,-2);

            OneResponse info = pool.info();
            if (info.isError()) {
                System.err.println(info.getErrorMessage());
                System.exit(1);
            }

            for (int i = 0; i < pool.getLength(); i++) {
                VirtualMachine vm = (VirtualMachine) pool.item(i);

                if (vm.getName().startsWith(searchName)) {

                    OneResponse monitor = vm.monitoring();

                    List<String> items = Lists.newArrayList();
                    for (String item : views) {
                        if (item.equals("name")) {
                            items.add(vm.getName());
                        } else if (item.equals("ip")) {
                            items.add(getIPFromXML(monitor.getMessage()));
                        } else if (item.equals("id")) {
                            items.add(vm.getId());
                        } else {
                            System.err.println("Unkown view item: " + item);
                            System.exit(1);
                            //TODO: Usage
                        }
                    }
                    System.out.println(Joiner.on("\t").join(items));
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

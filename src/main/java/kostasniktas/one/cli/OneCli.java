package kostasniktas.one.cli;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.opennebula.client.Client;
import org.opennebula.client.OneResponse;
import org.opennebula.client.vm.VirtualMachine;
import org.opennebula.client.vm.VirtualMachinePool;

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
    
    public static void main (String... args) {
        if (System.getenv("ONE_AUTH") == null || System.getenv("ONE_XMLRPC") == null) {
            //TODO: usage
            System.err.println("ONE_AUTH must be a file location and ONE_XMLRPC must be the URL");
            System.exit(1);
        }
        
        String searchName = null;
        
        Options options = new Options();
        
        OptionGroup optionGroupSearch = new OptionGroup();
        optionGroupSearch.addOption(new Option(null, "search-name", true, "Search for ONE nodes by name"));
        optionGroupSearch.setRequired(true);
        options.addOptionGroup(optionGroupSearch);
        
        Option optionView = new Option("v", "view", true, "The information to display for information found");
        options.addOption(optionView);
        
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.hasOption("search-name")) {
                searchName = commandLine.getOptionValue("search-name");
            }

        } catch (ParseException exp) {
            System.err.println("Parsing failed. Reason: " + exp.getMessage());
            System.exit(1);
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
                if (vm.getName().equals(searchName)) {
                    OneResponse monitor = vm.monitoring();
                    System.err.println(vm.getName() + "\t" + getIPFromXML(monitor.getMessage()));
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

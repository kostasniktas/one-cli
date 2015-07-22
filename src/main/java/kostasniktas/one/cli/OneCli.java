package kostasniktas.one.cli;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                //vm.info();
                //System.err.println(vm.getId() + "\t" + vm.getName());
                if (vm.getName().equals("somenode")) {
                    OneResponse monitor = vm.monitoring();
                    System.err.println("IP: " + getIPFromXML(monitor.getMessage()));
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

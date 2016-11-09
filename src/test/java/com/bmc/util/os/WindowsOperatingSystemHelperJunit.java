package com.bmc.util.os;

/*
 * 
 * import junit.framework.TestCase;
 * 
 * import org.apache.log4j.Logger; import org.apache.log4j.xml.DOMConfigurator;
 * 
 * public class WindowsOperatingSystemHelperJunit extends TestCase{
 * 
 * static Logger logger = Logger.getLogger(WindowsOperatingSystemHelper.class);
 * 
 * @BeforeClass public static void setUpBeforeClass() {
 * DOMConfigurator.configure(WindowsOperatingSystemHelper.class.getResource(
 * "/conf/log4j-config.xml"));
 * 
 * 
 * }
 * 
 * @AfterClass public static void tearDownAfterClass() {
 * 
 * }
 * 
 * @Before public void setUp() throws SQLException {
 * 
 * Properties prop = new Properties(); InputStream input = null; try { input =
 * new FileInputStream("conf\\junit.properties");
 * 
 * // load a properties file prop.load(input);
 * 
 */
/*
 * // get the property value and print it out service_start_name =
 * prop.getProperty("OS.WINDOWS.TEST.SERVICE_START_NAME"); service_stop_name =
 * prop.getProperty("OS.WINDOWS.TEST.SERVICE_STOP_NAME"); task_kill_name =
 * prop.getProperty("OS.WINDOWS.TEST.TASKKILL_NAME"); task_kill_filter=
 * prop.getProperty("OS.WINDOWS.TEST.TASKKILL_FILTER"); windows_command =
 * prop.getProperty("OS.WINDOWS.TEST.COMMAND");
 * windows_batch_command="tests//"+prop.getProperty(
 * "OS.WINDOWS.TEST.WINDOWS.BATCH");
 * logger.info(" service_start_name ="+service_start_name);
 * logger.info(" service_stop_name ="+service_stop_name);
 * logger.info(" task_kill_name ="+task_kill_name);
 * logger.info(" task_kill_filter ="+task_kill_filter);
 * logger.info(" windows_command ="+windows_command);
 * logger.info(" windows_batch_command ="+windows_batch_command);
 * 
 * } catch (Exception ae) { ae.printStackTrace();
 * 
 * } finally { if (input != null) { try { input.close(); } catch (IOException e)
 * { e.printStackTrace(); } } }
 * 
 * 
 * }
 * 
 * @After public void tearDown() {
 * 
 * } /*
 * 
 * @Test public void test() throws Exception {
 * 
 * logger.info(" service_stop_name ="+WindowsOperatingSystemHelper.startService(
 * service_stop_name));
 * logger.info(" Service start test "+WindowsOperatingSystemHelper.startService(
 * service_start_name)); logger.info(" task_kill_name ="+
 * WindowsOperatingSystemHelper.killTask(task_kill_name));
 * logger.info(" task_kill_name ="+
 * WindowsOperatingSystemHelper.killTask("",task_kill_filter));
 * logger.info(" windows_command ="+WindowsOperatingSystemHelper.
 * executeWindowsCommand(windows_command));
 * logger.info(" windows_batch_command ="+WindowsOperatingSystemHelper.
 * executeWindowsCommand(windows_batch_command)); }
 */
/*
 * @Test public void testServiceStop() throws Exception { boolean returnVal =
 * os.stopService(service_stop_name);
 * 
 * if (!returnVal) throw new Exception(" Service " + service_start_name +
 * " can not be started");
 * logger.info(" service_stop_name ="+os.stopService(service_stop_name)); }
 * 
 * @Test public void testServiceStart() throws Exception { boolean returnVal =
 * os.startService(service_start_name);
 * 
 * if (!returnVal) throw new Exception(" Service " + service_start_name +
 * " can not be started");
 * 
 * logger.info(" Service start test " + returnVal); }
 * 
 * @Test public void testTaskKill() throws Exception {
 * logger.info(" task_kill_name ="+ os.killProcess(task_kill_name));
 * logger.info(" task_kill_name ="+ os.killProcess("",task_kill_filter)); }
 * 
 * @Test public void testWindowsCommand() throws Exception {
 * logger.info(" windows_command ="+os.executeCommand(windows_command)); }
 * 
 * @Test public void testWindowsBatch() throws Exception {
 * logger.info(" windows_batch_command ="+os.executeCommand(
 * windows_batch_command));
 * 
 * } private static String service_start_name; private static String
 * service_stop_name; private static String task_kill_name; private static
 * String task_kill_filter; private static String windows_command; private
 * static String windows_batch_command; OperatingSystem os=
 * OperatingSystemHelper.getInstance();
 * 
 * }
 */
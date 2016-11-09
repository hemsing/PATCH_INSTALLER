
cd /opt/BMCSof*/BMCPor*t
mv appserver/websdk/tools/jboss/server/all/deploy/websdk.sar/lib/jcifs-1.3.18.jar appserver/websdk/tools/jboss/server/all/deploy/websdk.sar/lib/jcifs-0.8.3.jar
mv appserver/websdk/tools/jboss/server/all/modules/drmop.sar/lib/patsdk-application-class-editor-2.11.00.000.004.jar appserver/websdk/tools/jboss/server/all/modules/drmop.sar/lib/patsdk-application-class-editor-2.11.00_Build_605.jar
mv appserver/websdk/tools/jboss/server/all/modules/drmop.sar/lib/patsdk-impl-2.11.00.000.005.jar appserver/websdk/tools/jboss/server/all/modules/drmop.sar/lib/patsdk-impl-2.11.00_Build_605.jar
mv appserver/websdk/tools/jboss/server/all/deploy/admin-console.war/WEB-INF/lib/commons-collections-3.2.2.jar appserver/websdk/tools/jboss/server/all/deploy/admin-console.war/WEB-INF/lib/commons-collections-3.2.jar
mv /opt/BMCSoftware/BMCPortalKit/appserver/websdk/tools/jboss/common/lib/commons-collections-3.2.2.jar /opt/BMCSoftware/BMCPortalKit/appserver/websdk/tools/jboss/common/lib/commons-collections.jar
cd /tmp/testing
sh BPM_patch_2.11.00.000.010_install.sh
cp appserver/websdk/tools/jdk/bin/java appserver/websdk/tools/jdk/bin/java/BMCPortal
chmod 755 appserver/websdk/tools/jdk/bin/java/BMCPortal
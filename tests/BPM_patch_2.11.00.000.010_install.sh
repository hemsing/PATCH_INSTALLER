BMC_PORTAL_KIT_HOME=/opt/BMCSoftware/BMCPortalKit
export BMC_PORTAL_KIT_HOME
PATH=$BMC_PORTAL_KIT_HOME/appserver/websdk/tools/jdk/bin:$PATH
JAVA_HOME=$BMC_PORTAL_KIT_HOME/appserver/websdk/tools/jdk
export JAVA_HOME
export PATH
echo Using $JAVA_HOME/bin/java 
java -cp .:patch-installer-1.0.jar:log4j-1.2.17.jar:ojdbc-7.jar com.bmc.install.patchinstaller.PatchInstaller BPM_patch_2.11.00.000.010_PortalPatch.properties 
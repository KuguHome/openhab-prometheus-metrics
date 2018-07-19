## openHAB bundle for Prometheus metrics

### Development with Eclipse IDE

* Setting up the openHAB IDE

```
https://www.openhab.org/docs/developer/development/ide.html#prerequisites
```

You should have your IDE almost the same as on picture (if you select all projects to import in bullet above):
![Eclipse IDE with OH projects](img/eclipse_ide_oh.png =500x)

We would suggest to import not all to increase local build process:
![Eclipse IDE with OH projects (ligth)](img/eclipse_ide_oh_light.png =500x)
 and plus 2 plugins at the bottom

* Import Kugu projects into IDE:
File -> Open Projects From File System...
Select projects and import
Your IDE should be like here:
![ide with kugu projects](img/ide_with_kugu.png =500x)
  
* Set active Target
Open com.kuguhome.openhab.prometheusmetrics.targeteclipse.target file in Eclipse IDE
Change locations of the bundles to the path on your local machine
Save the file
Click Reload
Click Set as Active Target Platform
![set active target](img/set_active_target.png =500x)

* Select Debug configuration
Click Debug -> Debug Configuration and select all plugins on third tab which will be present after typing "prom" in textbox
![debug_config](img/debug_config.png =500x)
Click Apply and Debug

* You should see the log in the console appeared after the build and non HTTP 404 status on page
```
http://localhost:8080/rest/metrics/prometheus
```

### Production development with Maven
* Clean artifacts on root of project and create a repository for .target project
```
mvn clean
cd com.kuguhome.openhab.prometheusmetrics.p2site
mvn p2:site
```

* Run packaging
```
cd ../com.kuguhome.openhab.prometheusmetrics
mvn package
```

* Copy prometheus bundles and com.kuguhome.openhab.prometheusmetrics bundle into ```addon``` folder and start openHAB runtime
```
cp com.kuguhome.openhab.prometseusmetrics.target/bundles/simpleclient-0.4.0.jar ~/dev/openhab-2.3.0/addons/
cp com.kuguhome.openhab.prometseusmetrics.target/bundles/simpleclient_common-0.4.0.jar ~/dev/openhab-2.3.0/addons/
cp com.kuguhome.openhab.prometseusmetrics/target/com.kuguhome.openhab.prometheusmetrics-2.4.0-SNAPSHOT.jar ~/dev/openhab-2.3.0/addons/
cd ~/dev/openhab-2.3.0/
./start_debug.sh
```

* Also will be useful to remove cache of the runtime:
```
cd ~/dev/openhab-2.3.0/userdata/
rm -rf !(etc)
```

* You should see the log in the console appeared after and non HTTP 404 status on page
```
http://localhost:8080/rest/metrics/prometheus
```

Several files which you should consider:
```
startup.properties
```

- for loading bundles at startup from maven repo
```
org.ops4j.pax.logging.cfg
```

- for logging properties


* Add repository created with p2:site goal
![p2 site repository](img/p2_site_repo.png =500x)

### Possible issues and problems

* Version absence
![version absence issue](img/version_absence_issue.png =500x)
check your MANIFEST file for every artifact in the list and put appropriate version. Example:
```
ch.qos.logback.classic;version="1.0.7",
```

* Package sun.misc
![sun.misc issue](img/sun_misc_issue.png =500x)
We created separate bundle fragment to deal with this problem. Check if this fragment added to your Target
![debug_config](img/debug_config.png =500x)

* Target file error
You can face with such error during work with .target file:
![target platform error](img/target_platform_error.png =500x)
We didn't resolve this problem so there is one way to return to stable environment - to select default .target file
You can find it the project Infrastructure -> launch -> openhab.target
Then here you can add bundle folder with necessary bundles


### Resources
* Documentation of prometheus' data format https://prometheus.io/docs/concepts/metric_types/
* Developing Bindings https://www.eclipse.org/smarthome/documentation/development/bindings/how-to.html
* Java library for prometheus https://github.com/prometheus/client_java
* HABmin as a reference how to implement a UI https://github.com/openhab/org.openhab.ui.habmin

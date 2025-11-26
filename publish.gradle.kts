apply(plugin = "com.vanniktech.maven.publish")

group = "com.huanggenghg.easyipc"
version = "0.0.1"

afterEvaluate {
    configure<PublishingExtension> {
        publications {
            // Creates a Maven publication called "release".
            create<MavenPublication>("release") {
                // Applies the component for the release build variant.\
                // from(components["release"])
                // You can then customize attributes of the publication as shown below.
                groupId = (group.toString())
                version = version
            }
        }
    }
}
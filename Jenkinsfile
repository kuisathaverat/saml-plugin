//def buildConfiguration = buildPlugin.recommendedConfigurations()

def lts = "2.249.3"
def weekly = "2.266"
def buildConfiguration = [
/*
  [ platform: "linux",   jdk: "8", jenkins: lts, javaLevel: "8" ],
  [ platform: "windows", jdk: "8", jenkins: lts, javaLevel: "8" ],
  [ platform: "linux",   jdk: "11", jenkins: lts, javaLevel: "8" ],
  [ platform: "windows", jdk: "11", jenkins: lts, javaLevel: "8" ],
*/
  // Also build on recent weekly
  [ platform: "linux",   jdk: "11", jenkins: weekly, javaLevel: "8" ],
  [ platform: "windows", jdk: "11", jenkins: weekly, javaLevel: "8" ]
]

buildPlugin(configurations: buildConfiguration)

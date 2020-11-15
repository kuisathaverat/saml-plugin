//def buildConfiguration = buildPlugin.recommendedConfigurations()

def lts = "2.249.3"
def buildConfiguration = [
/*
  [ platform: "linux",   jdk: "8", jenkins: lts ],
  [ platform: "windows", jdk: "8", jenkins: lts ],
  [ platform: "linux",   jdk: "11", jenkins: lts ],
  [ platform: "windows", jdk: "11", jenkins: lts ],
*/
  // Also build on recent weekly
  [ platform: "linux",   jdk: "11" ],
  [ platform: "windows", jdk: "11" ]
]

buildPlugin(configurations: buildConfiguration)

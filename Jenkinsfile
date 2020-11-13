//def buildConfiguration = buildPlugin.recommendedConfigurations()

def lts = "2.249.3"
def weekly = "2.266"
def buildConfiguration = [
/*
  [ platform: "linux",   jdk: "8", jenkins: lts ],
  [ platform: "windows", jdk: "8", jenkins: lts ],
  [ platform: "linux",   jdk: "11", jenkins: lts ],
  [ platform: "windows", jdk: "11", jenkins: lts ],
*/
  // Also build on recent weekly
  [ platform: "linux",   jdk: "11", jenkins: weekly ],
  [ platform: "windows", jdk: "11", jenkins: weekly ]
]

buildPlugin(configurations: buildConfiguration)

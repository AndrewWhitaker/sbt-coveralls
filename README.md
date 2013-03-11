# xsbt-coverall-plugin

SBT plugin that uploaded scala code coverage to https://coveralls.io

WARNING - This plugin is new

# Requirements

 * [scct](http://mtkopone.github.com/scct/)

# Installation

1) Adding the following to your `project/build.sbt` file

    resolvers += Classpaths.typesafeResolver

    resolvers ++= Seq(
        "scct-github-repository" at "http://mtkopone.github.com/scct/maven-repo",
        "sonatype-oss-repo" at "https://oss.sonatype.org/content/groups/public/"
    )

    addSbtPlugin("reaktor" % "sbt-scct" % "0.2-SNAPSHOT")

    addSbtPlugin("com.github.theon" %% "xsbt-coveralls-plugin" % "0.0.1-SNAPSHOT")

2) Add the following to your `build.sbt`

    seq(ScctPlugin.instrumentSettings : _*)

3) Register on `https://coveralls.io/` and get a repo token

4) Write your token into the file `~/.sbt/coveralls.repo.token`

# Usage

In the SBT console run the command `coveralls test`. This should run your test suite, generate code coverage reports and upload the reports to `coveralls.io`. After running the command, you should see output similar to the following:

    Uploading to coveralls.io succeeded: Job #17.1
    https://coveralls.io/jobs/12207

# License

`xsbt-coveralls-plugin` is open source software released under the [Apache 2 License](http://www.apache.org/licenses/LICENSE-2.0).
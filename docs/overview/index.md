---
id: overview_index
title: "Contents"
---

 - **[Basics](basics.md)** — Data modeling

## Installation

Include ZIO Actors in your project by adding the following to your `build.sbt`:

```scala mdoc:passthrough

println(s"""```""")
if (zio.actors.BuildInfo.isSnapshot)
  println(s"""resolvers += Resolver.sonatypeRepo("snapshots")""")
println(s"""libraryDependencies += "org.shoonya" %% "shoonya" % "${shoonya.BuildInfo.version}"""")
println(s"""```""")

```
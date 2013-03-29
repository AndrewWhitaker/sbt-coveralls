package com.github.theon.coveralls

import java.io.File
import io.Source
import org.codehaus.jackson.{JsonEncoding, JsonFactory}
import org.codehaus.jackson.io.JsonStringEncoder
import GitClient._
import sbt.State
import annotation.tailrec

trait CoverallPayloadWriter {

  def file:String
  def repoToken:String
  def travisJobId:Option[String]

  val gen = generator(file)
  val stringEncoder = new JsonStringEncoder()

  def generator(file:String) = {
    val factory = new JsonFactory()
    factory.createJsonGenerator(new File(file), JsonEncoding.UTF8);
  }

  def start(implicit state: State) {
    gen.writeStartObject
    gen.writeStringField("repo_token", repoToken)

    if(travisJobId.isDefined){
      gen.writeStringField("service_name", "travis-ci")
      gen.writeStringField("service_job_id", travisJobId.get)
    }

    addGitInfo

    gen.writeFieldName("source_files")
    gen.writeStartArray
  }

  private def addGitInfo(implicit state: State) {
    gen.writeFieldName("git")
    gen.writeStartObject

    gen.writeFieldName("head")
    gen.writeStartObject

    gen.writeStringField("id", lastCommit("%h"))
    gen.writeStringField("author_name", lastCommit("%an"))
    gen.writeStringField("author_email", lastCommit("%ae"))
    gen.writeStringField("committer_name", lastCommit("%cn"))
    gen.writeStringField("committer_email", lastCommit("%ce"))
    gen.writeStringField("message", lastCommit("%s"))

    gen.writeEndObject

    gen.writeStringField("branch", currentBranch)

    gen.writeFieldName("remotes")
    gen.writeStartArray

    addGitRemotes(remotes)

    gen.writeEndArray

    gen.writeEndObject
  }

  @tailrec
  private def addGitRemotes(remotes:Seq[String])(implicit state: State) {
    if(remotes.isEmpty) return

    gen.writeStartObject
    gen.writeStringField("name", remotes.head)
    gen.writeStringField("url", remoteUrl(remotes.head))
    gen.writeEndObject

    addGitRemotes(remotes.tail)
  }

  def addSouceFile(report:SourceFileReport) {
    gen.writeStartObject
    gen.writeStringField("name", report.file)

    val source = Source.fromFile(report.file).mkString
    gen.writeStringField("source", source)

    gen.writeFieldName("coverage")
    gen.writeStartArray
    report.lineCoverage.foreach(hit => {
      hit match {
        case Some(x) => gen.writeNumber(x)
        case _ => gen.writeNull()
      }
    })
    gen.writeEndArray
    gen.writeEndObject
  }

  def end() {
    gen.writeEndArray
    gen.writeEndObject
    gen.flush()
  }
}

package com.metamx.common.scala

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.metamx.common.scala.Predef._
import java.io.{OutputStream, Writer}

object Jackson extends Jackson

trait Jackson
{
  private val objectMapper = newObjectMapper()

  def parse[A: ClassManifest](s: String): A = {
    objectMapper.readValue(s, implicitly[ClassManifest[A]].erasure.asInstanceOf[Class[A]])
  }

  def parse[A: ClassManifest](bs: Array[Byte]): A = {
    objectMapper.readValue(bs, implicitly[ClassManifest[A]].erasure.asInstanceOf[Class[A]])
  }

  def generate[A](a: A): String = objectMapper.writeValueAsString(a)

  def generate[A](a: A, writer: Writer) {
    objectMapper.writeValue(writer, a)
  }

  def generate[A](a: A, stream: OutputStream) {
    objectMapper.writeValue(stream, a)
  }

  def bytes[A](a: A): Array[Byte] = objectMapper.writeValueAsBytes(a)

  def pretty[A](a: A): String = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(a)

  def pretty[A](a: A, writer: Writer) {
    objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, a)
  }

  def pretty[A](a: A, stream: OutputStream) {
    objectMapper.writerWithDefaultPrettyPrinter().writeValue(stream, a)
  }

  def normalize[A : ClassManifest](a: A) = parse[A](generate(a))

  def newObjectMapper() = {
    new ObjectMapper withEffect {
      jm =>
        jm.registerModule(new JodaModule)
        jm.registerModule(DefaultScalaModule)
    }
  }
}
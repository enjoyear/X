package com.chen.guo.models

object RequestType extends Enumeration {
  type RequestType = Value
  val CODE, NAME = Value

  def apply(typeName: String) = RequestType.withName(typeName.toUpperCase)
}

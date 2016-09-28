package com.chen.guo.models.learn

case class Customer(name: String)

case class Order(title: String)

case class UserData(name: String, age: Int)

case class Contact(firstname: String, lastname: String, company: Option[String], informations: Seq[ContactInformation])

object Contact {
  def save(contact: Contact): Int = 99
}

case class ContactInformation(label: String, email: Option[String], phones: List[String])
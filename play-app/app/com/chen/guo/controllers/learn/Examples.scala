package com.chen.guo.controllers.learn

import javax.inject.{Inject, Singleton}

import com.chen.guo.models.learn.{Contact, ContactInformation, Customer, Order}
import com.chen.guo.views.html.learn._
import play.api.data.Form
import play.api.mvc.{Action, Controller}
import play.twirl.api.Html

@Singleton
class Examples @Inject() extends Controller {

  //  val contactForm: Form[Contact] = Form(
  //    // Defines a mapping that will handle Contact values
  //    mapping(
  //      "firstname" -> nonEmptyText,
  //      "lastname" -> nonEmptyText,
  //      "company" -> optional(text),
  //
  //      // Defines a repeated mapping
  //      "informations" -> seq(
  //        mapping(
  //          "label" -> nonEmptyText,
  //          "email" -> optional(email),
  //          "phones" -> list(
  //            text verifying pattern("""[0-9.+]+""".r, error = "A valid phone number is required")
  //          )
  //        )(ContactInformation.apply)(ContactInformation.unapply)
  //      )
  //    )(Contact.apply)(Contact.unapply)
  //  )

  def showCustomerOrders(customer: String) = Action {
    val html: Html = Html("<h1>Sidebar</h1>")
    Ok(plaintext(Customer(customer), List(Order("Ord-1"), Order("Ord-2")))
    (html)(Html(foo().body)))
  }

  //  def editContact = Action {
  //    val existingContact = Contact(
  //      "Fake", "Contact", Some("Fake company"), informations = List(
  //        ContactInformation(
  //          "Personal", Some("fakecontact@gmail.com"), List("01.23.45.67.89", "98.76.54.32.10")
  //        ),
  //        ContactInformation(
  //          "Professional", Some("fakecontact@company.com"), List("01.23.45.67.89")
  //        ),
  //        ContactInformation(
  //          "Previous", Some("fakecontact@oldcompany.com"), List()
  //        )
  //      )
  //    )
  //    Ok(views.html.contact.form(contactForm.fill(existingContact)))
  //  }
  //
  //  def saveContact = Action { implicit request =>
  //    contactForm.bindFromRequest.fold(
  //      formWithErrors => {
  //        BadRequest(views.html.contact.form(formWithErrors))
  //      },
  //      contact => {
  //        val contactId = Contact.save(contact)
  //        Redirect(routes.Application.showContact(contactId)).flashing("success" -> "Contact saved!")
  //      }
  //    )
  //  }
}

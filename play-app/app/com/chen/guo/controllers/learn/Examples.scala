package com.chen.guo.controllers.learn

import javax.inject.{Inject, Singleton}

import com.chen.guo.models.learn.{Customer, Order}
import com.chen.guo.views.html.learn._
import play.api.mvc.{Action, Controller}
import play.twirl.api.Html

@Singleton
class Examples @Inject() extends Controller {
  def showCustomerOrders(customer: String) = Action {
    val html: Html = Html("<h1>Sidebar</h1>")
    Ok(plaintext(Customer(customer), List(Order("Ord-1"), Order("Ord-2")))
    (html)(Html(foo().body)))
  }
}

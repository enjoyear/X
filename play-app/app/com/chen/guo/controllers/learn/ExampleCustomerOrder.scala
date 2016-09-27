package com.chen.guo.controllers.learn

import javax.inject.{Inject, Singleton}

import com.chen.guo.models.{Customer, Order}
import play.api.mvc.{Action, Controller}
import play.twirl.api.Html
import com.chen.guo.views.html.learn._

@Singleton
class ExampleCustomerOrder @Inject() extends Controller {

  def showCustomerOrders = Action {
    val html: Html = Html("<h1>Sidebar</h1>")
    Ok(plaintext(Customer("Chen"), List(Order("Ord-1"), Order("Ord-2")))
    (html)(Html(foo().body)))
  }
}

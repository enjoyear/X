package com.chen.guo.controllers.learn

import javax.inject.{Inject, Singleton}

import com.chen.guo.models.learn.UserData
import com.chen.guo.views.html.learn._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, Controller}

@Singleton
class Forms @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  val userForm = Form(
    mapping(
      "name" -> text,
      "age" -> number
    )(UserData.apply)(UserData.unapply)
  )

  def formGet = Action {
    Ok(user(userForm))
  }

  def formPost = Action { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(user(formWithErrors))
      },
      userData => {
        /* binding success, you get the actual value. */
        val newUser = UserData(userData.name, userData.age)
        Redirect(routes.Examples.showCustomerOrders(newUser.name))
      }
    )
  }
}

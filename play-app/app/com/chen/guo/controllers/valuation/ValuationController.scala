package com.chen.guo.controllers.valuation

import javax.inject.{Inject, Singleton}

import play.api.data.Forms._
import com.chen.guo.models.valuation.SingleRequest
import play.api.data.Form
import play.api.data.Forms.mapping
import play.api.mvc.{Action, Controller}
import com.chen.guo.views.html.valuation.valuation


import play.api.data._
import play.api.i18n.{I18nSupport, MessagesApi}


@Singleton
class ValuationController @Inject() extends Controller {

  val requestForm = Form(
    mapping(
      "theType" -> text,
      "name" -> text
    )(SingleRequest.apply)(SingleRequest.unapply)
  )

  def formGet = Action {
    Ok(valuation(requestForm))
  }
}

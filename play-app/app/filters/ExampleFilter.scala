package filters

import akka.stream.Materializer
import javax.inject._
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

/**
  * This is a simple filter that adds a header to all requests. It's
  * added to the application's list of filters by the
  * [[Filters]] class.
  *
  * @param mat  This object is needed to handle streaming of requests
  *             and responses.
  * @param exec This class is needed to execute code asynchronously.
  *             It is used below by the `map` method.
  */
@Singleton
class ExampleFilter @Inject()(implicit override val mat: Materializer,
                              exec: ExecutionContext) extends Filter {

  override def apply(nextFilter: RequestHeader => Future[Result])
                    (requestHeader: RequestHeader): Future[Result] = {
    // Run the next filter in the chain. This will call other filters
    // and eventually call the action. Take the result and modify it
    // by adding a new header.
    println("Example Filter:" + requestHeader)
    nextFilter(requestHeader).map { result =>
      println("Example Filter:" + result)
      val headers: Result = result.withHeaders("X-ExampleFilter" -> "foo")
      println(headers)
      headers
    }
  }

}

@Singleton
class LoggingFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {
    println("Logging Filter:" + requestHeader)
    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>

      val endTime = System.currentTimeMillis
      val requestTime = endTime - startTime

      println(s"${requestHeader.method} ${requestHeader.uri} took ${requestTime}ms and returned ${result.header.status}")

      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }
}

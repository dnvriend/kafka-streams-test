package auth

import com.lightbend.lagom.scaladsl.server.ServerServiceCall
import com.lightbend.lagom.scaladsl.api.transport.{RequestHeader, ResponseHeader}

import scala.concurrent.Future
import scala.util.Try

object LoggingServiceCall {
  def logged[Request, Response](serviceCall: ServerServiceCall[Request, Response]) =
    ServerServiceCall.compose { (requestHeader: RequestHeader) =>
      println(s"Received ${requestHeader.method} ${requestHeader.uri}")
      serviceCall
  }
}

// Code generated by connect-kotlin. DO NOT EDIT.
//
// Source: buf/connect/demo/eliza/v1/eliza.proto
//
package buf.connect.demo.eliza.v1

import build.buf.connect.BidirectionalStreamInterface
import build.buf.connect.Headers
import build.buf.connect.MethodSpec
import build.buf.connect.ProtocolClientInterface
import build.buf.connect.ResponseMessage
import build.buf.connect.ServerOnlyStreamInterface

/**
 *  ElizaService provides a way to talk to the ELIZA, which is a port of
 *  the DOCTOR script for Joseph Weizenbaum's original ELIZA program.
 *  Created in the mid-1960s at the MIT Artificial Intelligence Laboratory,
 *  ELIZA demonstrates the superficiality of human-computer communication.
 *  DOCTOR simulates a psychotherapist, and is commonly found as an Easter
 *  egg in emacs distributions.
 */
public class ElizaServiceClient(
  private val client: ProtocolClientInterface,
) : ElizaServiceClientInterface {
  /**
   *  Say is a unary request demo. This method should allow for a one sentence
   *  response given a one sentence request.
   */
  public override suspend fun say(request: Eliza.SayRequest, headers: Headers):
      ResponseMessage<Eliza.SayResponse> = client.unary(
    request,
    headers,
    MethodSpec(
    "buf.connect.demo.eliza.v1.ElizaService/Say",
      buf.connect.demo.eliza.v1.Eliza.SayRequest::class,
      buf.connect.demo.eliza.v1.Eliza.SayResponse::class
    ),
  )


  /**
   *  Converse is a bi-directional streaming request demo. This method should allow for
   *  many requests and many responses.
   */
  public override suspend fun converse(headers: Headers):
      BidirectionalStreamInterface<Eliza.ConverseRequest, Eliza.ConverseResponse> = client.stream(
    headers,
    MethodSpec(
    "buf.connect.demo.eliza.v1.ElizaService/Converse",
      buf.connect.demo.eliza.v1.Eliza.ConverseRequest::class,
      buf.connect.demo.eliza.v1.Eliza.ConverseResponse::class
    ),
  )


  /**
   *  Introduce is a server-streaming request demo.  This method allows for a single request that
   * will return a series
   *  of responses
   */
  public override suspend fun introduce(headers: Headers):
      ServerOnlyStreamInterface<Eliza.IntroduceRequest, Eliza.IntroduceResponse> =
      client.serverStream(
    headers,
    MethodSpec(
    "buf.connect.demo.eliza.v1.ElizaService/Introduce",
      buf.connect.demo.eliza.v1.Eliza.IntroduceRequest::class,
      buf.connect.demo.eliza.v1.Eliza.IntroduceResponse::class
    ),
  )

}

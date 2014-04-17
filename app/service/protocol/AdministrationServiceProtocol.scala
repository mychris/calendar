package service.protocol

/*
 * Requests
 */

case object CreateSchema extends Request

case object DropSchema extends Request

case object CreateSampleData extends Request

/*
 * Responses
 */
 
case object SchemaCreated extends Success

case object SchemaDropped extends Success

case object SampleDataCreated extends Success
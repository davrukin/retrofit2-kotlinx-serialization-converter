package com.jakewharton.retrofit2.converter.kotlinx.serialization

import java.lang.reflect.Type
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.serializer
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody

internal sealed class Serializer {
  abstract fun <T> fromResponseBody(loader: DeserializationStrategy<T>, body: ResponseBody, onEach: (T) -> Unit = {}): T
  abstract fun <T> toRequestBody(contentType: MediaType, saver: SerializationStrategy<T>, value: T): RequestBody

  protected abstract val format: SerialFormat

  fun serializer(type: Type): KSerializer<Any> = format.serializersModule.serializer(type)

  class FromString(override val format: StringFormat) : Serializer() {
    override fun <T> fromResponseBody(loader: DeserializationStrategy<T>, body: ResponseBody, onEach: (T) -> Unit): T {
      val string = body.string()
      val t = format.decodeFromString(loader, string)
	    onEach.invoke(t)
	    return t
    }

    override fun <T> toRequestBody(contentType: MediaType, saver: SerializationStrategy<T>, value: T): RequestBody {
      val string = format.encodeToString(saver, value)
      return RequestBody.create(contentType, string)
    }
  }

  class FromBytes(override val format: BinaryFormat) : Serializer() {
    override fun <T> fromResponseBody(loader: DeserializationStrategy<T>, body: ResponseBody, onEach: (T) -> Unit): T {
      val bytes = body.bytes()
      val t = format.decodeFromByteArray(loader, bytes)
	    onEach.invoke(t)
	    return t
    }

    override fun <T> toRequestBody(contentType: MediaType, saver: SerializationStrategy<T>, value: T): RequestBody {
      val bytes = format.encodeToByteArray(saver, value)
      return RequestBody.create(contentType, bytes)
    }
  }
}

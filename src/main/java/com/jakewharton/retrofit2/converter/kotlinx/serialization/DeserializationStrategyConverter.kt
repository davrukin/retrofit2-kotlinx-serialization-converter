package com.jakewharton.retrofit2.converter.kotlinx.serialization

import kotlinx.serialization.DeserializationStrategy
import okhttp3.ResponseBody
import retrofit2.Converter

internal class DeserializationStrategyConverter<T>(
  private val loader: DeserializationStrategy<T>,
  private val serializer: Serializer,
	private val onEach: (T) -> Unit = {},
) : Converter<ResponseBody, T> {
  override fun convert(value: ResponseBody) = serializer.fromResponseBody(loader, value, onEach)
}

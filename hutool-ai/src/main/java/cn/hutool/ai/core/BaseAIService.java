/*
 * Copyright (c) 2025 Hutool Team and hutool.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hutool.ai.core;

import cn.hutool.ai.AIException;
import cn.hutool.http.*;

import java.util.Map;

/**
 * 基础AIService，包含基公共参数和公共方法
 *
 * @author elichow
 * @since 5.8.38
 */
public class BaseAIService {

	protected final AIConfig config;

	/**
	 * 构造方法
	 *
	 * @param config AI配置
	 */
	public BaseAIService(final AIConfig config) {
		this.config = config;
	}

	/**
	 * 发送Get请求
	 * @param endpoint 请求节点
	 * @return 请求响应
	 */
	protected HttpResponse sendGet(final String endpoint) {
		//链式构建请求
		try {
			//设置超时3分钟
			return HttpRequest.get(config.getApiUrl() + endpoint)
				.header(Header.ACCEPT, "application/json")
				.header(Header.AUTHORIZATION, "Bearer " + config.getApiKey())
				.timeout(180000)
				.execute();
		} catch (final AIException e) {
			throw new AIException("Failed to send GET request: " + e.getMessage(), e);
		}
	}

	/**
	 * 发送Post请求
	 * @param endpoint 请求节点
	 * @param paramJson 请求参数json
	 * @return 请求响应
	 */
	protected HttpResponse sendPost(final String endpoint, final String paramJson) {
		//链式构建请求
		try {
			return HttpRequest.post(config.getApiUrl() + endpoint)
				.header(Header.CONTENT_TYPE, "application/json")
				.header(Header.ACCEPT, "application/json")
				.header(Header.AUTHORIZATION, "Bearer " + config.getApiKey())
				.body(paramJson)
				.timeout(180000)
				.execute();
		} catch (final AIException e) {
			throw new AIException("Failed to send POST request：" + e.getMessage(), e);
		}

	}

	/**
	 * 发送表单请求
	 * @param endpoint 请求节点
	 * @param paramMap 请求参数map
	 * @return 请求响应
	 */
	protected HttpResponse sendFormData(final String endpoint, final Map<String, Object> paramMap) {
		//链式构建请求
		try {
			//设置超时3分钟
			return HttpRequest.post(config.getApiUrl() + endpoint)
				.header(Header.CONTENT_TYPE, "multipart/form-data")
				.header(Header.ACCEPT, "application/json")
				.header(Header.AUTHORIZATION, "Bearer " + config.getApiKey())
				.form(paramMap)
				.timeout(180000)
				.execute();
		} catch (final AIException e) {
			throw new AIException("Failed to send POST request：" + e.getMessage(), e);
		}
	}
}


package com.gupaoedu.example.nacosdubboprovider.dubbo.demo.provider;

import com.gupaoedu.example.api.EchoService;
import org.apache.dubbo.config.annotation.Service;

@Service(protocol = "dubbo")
class EchoServiceImpl implements EchoService {

	@Override
	public String echo(String message) {
		return "[echo] Hello, " + message;
	}

}

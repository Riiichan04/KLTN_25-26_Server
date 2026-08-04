package vn.id.nonglam.kltn.kltn.common.constants;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class RedirectUrl {
    @Value("${url.client}")
    public String clientUrl;
}

package com.example.demo.file.bucket;

import java.io.File;
import java.net.URI;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class BucketComponent {

  public void upload(File file, String key) {
    throw new UnsupportedOperationException("BucketComponent stub");
  }

  public byte[] download(String key) {
    throw new UnsupportedOperationException("BucketComponent stub");
  }

  public URI presign(String key, Duration ttl) {
    throw new UnsupportedOperationException("BucketComponent stub");
  }
}

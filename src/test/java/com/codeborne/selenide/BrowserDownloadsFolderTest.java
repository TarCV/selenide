package com.codeborne.selenide;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.Path;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.touch;
import static org.assertj.core.api.Assertions.assertThat;

final class BrowserDownloadsFolderTest {
  @Test
  void deletesAllFilesFromFolder(@TempDir Path folder) throws IOException {
    touch(new Path(folder, "file1"));
    touch(new Path(folder, "file2"));

    BrowserDownloadsFolder.from(folder).cleanupBeforeDownload();

    assertThat(folder).exists();
    assertThat(new Path(folder, "file1")).doesNotExist();
    assertThat(new Path(folder, "file2")).doesNotExist();
  }
}

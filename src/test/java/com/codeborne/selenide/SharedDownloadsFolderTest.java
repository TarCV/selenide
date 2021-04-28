package com.codeborne.selenide;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.Path;
import java.io.IOException;

import static org.apache.commons.io.FileUtils.touch;
import static org.assertj.core.api.Assertions.assertThat;

final class SharedDownloadsFolderTest {
  @Test
  void shouldNotDeleteAnyFiles(@TempDir Path folder) throws IOException {
    touch(new Path(folder, "file1"));
    touch(new Path(folder, "file2"));

    new SharedDownloadsFolder(folder.getAbsolutePath()).cleanupBeforeDownload();

    assertThat(new Path(folder, "file1")).exists();
    assertThat(new Path(folder, "file2")).exists();
  }
}

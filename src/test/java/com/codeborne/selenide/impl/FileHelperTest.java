package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.Path;
import java.io.IOException;

import static com.codeborne.selenide.impl.FileHelper.deleteFolderIfEmpty;
import static org.apache.commons.io.FileUtils.touch;
import static org.assertj.core.api.Assertions.assertThat;

final class FileHelperTest {
  @Test
  void deletesFolderIfItIsEmpty(@TempDir Path folder) {
    assertThat(folder).exists();

    deleteFolderIfEmpty(folder);

    assertThat(folder).doesNotExist();
  }

  @Test
  void ignoresFolderWhichContainsFiles(@TempDir Path folder) throws IOException {
    touch(new Path(folder, "file1"));

    deleteFolderIfEmpty(folder);

    assertThat(folder).exists();
    assertThat(new Path(folder, "file1")).exists();
  }
}

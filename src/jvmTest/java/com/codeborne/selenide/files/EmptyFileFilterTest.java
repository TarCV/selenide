package com.codeborne.selenide.files;

import okio.Path;
import org.junit.jupiter.api.Test;

import java.io.File;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

final class EmptyFileFilterTest {
  private static final String FILTER_DESCRIPTION = "";
  private final FileFilter filter = new EmptyFileFilter();

  @Test
  void matchesAnyFile() {
    assertThat(filter.match(new DownloadedFile(Path.get(new File("")), emptyMap()))).isTrue();
    assertThat(filter.match(new DownloadedFile(Path.get(new File("cv.pdf")), emptyMap()))).isTrue();
    assertThat(filter.match(new DownloadedFile(Path.get(new File("cv1.pdf")), emptyMap()))).isTrue();
    assertThat(filter.match(new DownloadedFile(Path.get(new File(" cv.pdf")), emptyMap()))).isTrue();
    assertThat(filter.match(new DownloadedFile(Path.get(new File("cv.pdf ")), emptyMap()))).isTrue();
  }

  @Test
  void description() {
    assertThat(filter.description()).isEqualTo(FILTER_DESCRIPTION);
  }

  @Test
  void hasToString() {
    assertThat(filter).hasToString(FILTER_DESCRIPTION);
  }

  @Test
  void isEmpty() {
    assertThat(filter.isEmpty()).isTrue();
  }
}

/*
 * Copyright 2019 Apple Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.shazam.fork.reporter.injector;


import com.shazam.fork.reporter.FlakinessReportPrinter;
import com.shazam.fork.reporter.HtmlFlakinessReportPrinter;

import java.io.File;

import static com.shazam.fork.reporter.injector.ConfigurationInjector.configuration;
import static com.shazam.fork.reporter.injector.HtmlGeneratorInjector.htmlGenerator;
import static com.shazam.fork.reporter.injector.TestFlakinessToHtmlReportConverterInjector.converter;

public class FlakinessReportPrinterInjector {

    private FlakinessReportPrinterInjector() {}

    public static FlakinessReportPrinter flakinessReportPrinter() {
        File output = configuration().getOutput();
        return new HtmlFlakinessReportPrinter(output, htmlGenerator(), converter());
    }
}

package org.grobid.core.lang.impl;

import opennlp.tools.sentdetect.SentenceDetectorME; 
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

import org.grobid.core.lang.SentenceDetector;
import org.grobid.core.utilities.OffsetPosition;
import org.grobid.core.utilities.GrobidProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

/**
 * Implementation of sentence segmentation via OpenNLP
 * 
 */
public class OpenNLPSentenceDetector implements SentenceDetector {
    private static final Logger LOGGER  = LoggerFactory.getLogger(OpenNLPSentenceDetector.class);

    // components for sentence segmentation
    private SentenceDetectorME detector = null;

    public OpenNLPSentenceDetector() {
        // Loading sentence detector model
        String openNLPModelFile = GrobidProperties.getGrobidHomePath() + 
            File.separator + "lexicon" + File.separator + "openNLP" + File.separator + "en-sent.bin";
        try(InputStream inputStream = new FileInputStream(openNLPModelFile)) {
            SentenceModel model = new SentenceModel(inputStream);
            detector = new SentenceDetectorME(model);
        } catch(IOException e) {
            LOGGER.warn("Problem when loading the sentence segmenter", e);
        }
    }

    @Override
    public List<OffsetPosition> detect(String text) {
        Span spans[] = detector.sentPosDetect(text); 
        List<OffsetPosition> result = new ArrayList<>();

        // convert Span to OffsetPosition
        for(int i=0; i<spans.length; i++) {
            result.add(new OffsetPosition(spans[i].getStart(), spans[i].getEnd()));
        }         

        return result;
    }
}

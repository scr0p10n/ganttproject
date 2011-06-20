/*
 * Created on 28.05.2005
 */
package net.sourceforge.ganttproject.export;

import java.awt.Component;
import java.awt.image.RenderedImage;
import java.io.File;
import java.text.MessageFormat;
import java.util.Arrays;

import javax.imageio.ImageIO;

import net.sourceforge.ganttproject.chart.Chart;
import net.sourceforge.ganttproject.gui.options.model.EnumerationOption;
import net.sourceforge.ganttproject.gui.options.model.GPAbstractOption;
import net.sourceforge.ganttproject.gui.options.model.GPOption;
import net.sourceforge.ganttproject.gui.options.model.GPOptionGroup;
import net.sourceforge.ganttproject.language.GanttLanguage;

/**
 * @author bard
 */
public class ExporterToImage extends AbstractExporter {

    static class FileTypeOption extends GPAbstractOption<String> implements
            EnumerationOption {
        static final String[] FILE_FORMAT_ID = new String[] {
                "impex.image.fileformat.png", "impex.image.fileformat.jpeg" };

        static final String[] FILE_EXTENSION = new String[] { "png", "jpg" };

        // TODO GPAbstractOption already has this field, why add it again?!
        private String myValue = FileTypeOption.FILE_FORMAT_ID[0];

        FileTypeOption() {
            super("impex.image.fileformat");
        }

        public String[] getAvailableValues() {
            return FileTypeOption.FILE_FORMAT_ID;
        }

        public void setValue(String value) {
            myValue = value;
        }

        public String getValue() {
            return myValue;
        }

        String proposeFileExtension() {
            for (int i = 0; i < FileTypeOption.FILE_FORMAT_ID.length; i++) {
                if (myValue.equals(FileTypeOption.FILE_FORMAT_ID[i])) {
                    return FileTypeOption.FILE_EXTENSION[i];
                }
            }
            throw new IllegalStateException("Selected format=" + myValue
                    + " has not been found in known formats:"
                    + Arrays.asList(FileTypeOption.FILE_FORMAT_ID));
        }

        public String getPersistentValue() {
            return null;
        }

        public void loadPersistentValue(String value) {
        }

        public boolean isChanged() {
            return false;
        }
    }

    private final FileTypeOption myFileTypeOption = new FileTypeOption();

    private final GPOptionGroup myOptions = new GPOptionGroup("impex.image",
            new GPOption[] { myFileTypeOption });

    public ExporterToImage() {
        myOptions.setTitled(false);
    }

    public String getFileTypeDescription() {
        return MessageFormat.format(GanttLanguage.getInstance().getText(
                "impex.image.description"),
                new Object[] { proposeFileExtension() });
    }

    public GPOptionGroup getOptions() {
        return myOptions;
    }

    public GPOptionGroup[] getSecondaryOptions() {
        //return myGanttChart.getOptionGroups();
        return null;
    }

    public Component getCustomOptionsUI() {
        return null;
    }

    public String getFileNamePattern() {
        return proposeFileExtension();
    }

    public void run(File outputFile,ExportFinalizationJob finalizationJob) throws Exception {
        Chart chart = getUIFacade().getActiveChart();
        if (chart==null) {
            chart = getGanttChart();
        }
        RenderedImage renderedImage = chart.getRenderedImage(createExportSettings());
        ImageIO.write(renderedImage, myFileTypeOption.proposeFileExtension(),
                outputFile);
        finalizationJob.run(new File[] { outputFile });
    }

    public String proposeFileExtension() {
        return myFileTypeOption.proposeFileExtension();
    }

    public String[] getFileExtensions() {
        return FileTypeOption.FILE_EXTENSION;
    }

    public String[] getCommandLineKeys() {
        return getFileExtensions();
    }
}

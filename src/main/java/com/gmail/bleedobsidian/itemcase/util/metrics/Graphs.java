package com.gmail.bleedobsidian.itemcase.util.metrics;

import com.gmail.bleedobsidian.itemcase.configurations.ConfigFile;
import com.gmail.bleedobsidian.itemcase.util.metrics.Metrics.Graph;

/**
 * A metrics graph handler. (Only used internally)
 *
 * @author BleedObsidian (Jesse Prescott)
 */
public class Graphs {

    /**
     * Metrics instance.
     */
    private Metrics metrics;

    /**
     * ConfigFile.
     */
    private ConfigFile config;

    /**
     * New Graphs.
     *
     * @param metrics Metrics.
     * @param config Config.
     */
    public Graphs(Metrics metrics, ConfigFile config) {
        this.metrics = metrics;
        this.config = config;
    }

    /**
     * Create metrics graphs.
     */
    public void createGraphs() {
        this.graph_CheckForUpdate();
    }

    /**
     * Graph: Checking For An Update.
     */
    private void graph_CheckForUpdate() {
        Graph graph = metrics.createGraph("Servers Cheking For An Update");

        graph.addPlotter(new Metrics.Plotter("Checking For An Update") {
            @Override
            public int getValue() {
                if (config.getFileConfiguration().getBoolean(
                        "Updates.Check-For-Update")) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }
}

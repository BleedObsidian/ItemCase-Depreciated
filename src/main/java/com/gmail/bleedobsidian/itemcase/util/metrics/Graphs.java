package com.gmail.bleedobsidian.itemcase.util.metrics;

import com.gmail.bleedobsidian.itemcase.configurations.ConfigFile;
import com.gmail.bleedobsidian.itemcase.util.metrics.Metrics.Graph;

public class Graphs {
    private Metrics metrics;
    private ConfigFile config;

    public Graphs(Metrics metrics, ConfigFile config) {
        this.metrics = metrics;
        this.config = config;
    }

    public void createGraphs() {
        this.graph_CheckForUpdate();
    }

    private void graph_CheckForUpdate() {
        Graph graph = metrics.createGraph("Servers Cheking For An Update");

        graph.addPlotter(new Metrics.Plotter("Checking For Update") {
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

        graph.addPlotter(new Metrics.Plotter("Not Checking For Update") {
            @Override
            public int getValue() {
                if (!config.getFileConfiguration().getBoolean("CheckForUpdate")) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }
}

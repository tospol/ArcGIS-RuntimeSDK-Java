/*
 * Copyright 2016 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.esri.samples.scene.scene_layer;

import javafx.scene.control.Button;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.arcgisservices.TileInfo;
import com.esri.arcgisruntime.data.TileCache;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.RasterElevationSource;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Camera;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.LayerSceneProperties;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.raster.Raster;
import com.esri.arcgisruntime.symbology.ModelSceneSymbol;
import com.esri.arcgisruntime.symbology.Renderer;
import com.esri.arcgisruntime.symbology.RotationType;
import com.esri.arcgisruntime.symbology.SimpleRenderer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class SceneLayerSample extends Application {

  private SceneView sceneView;
  ArcGISScene scene;
  private static final String LOCAL_ELEVATION_IMAGE_SERVICE = "http://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer";
  private static final String BUILDINGS = "http://scene.arcgis" +
      ".com/arcgis/rest/services/Hosted/Buildings_Brest/SceneServer/layers/0";
  private static final SpatialReference WGS84 = SpatialReferences.getWgs84();
  final FileChooser fileChooser = new FileChooser();

  @Override
  public void start(Stage stage) throws Exception {

    try {

      // create stack pane and JavaFX app scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Scene Layer Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();
      // create a tile cache from a local tile package
      TileCache tileCache = new TileCache("D:/EP/tha_dem_srtm/prachaub.tpk");
      tileCache.addDoneLoadingListener(() -> {
   	   if (tileCache.getLoadStatus() == LoadStatus.LOADED) {
   	     // raster layer has loaded
   		Envelope env = tileCache.getFullExtent();
   		TileInfo tileInfo = tileCache.getTileInfo();
   		sceneView.setViewpoint(new Viewpoint(env));
   	   }
   	LoadStatus loaded = tileCache.getLoadStatus();
   	   ArcGISRuntimeException err = tileCache.getLoadError();
   	tileCache.getLoadStatus();
   	 });
      // create a tiled layer from the tile cache
      ArcGISTiledLayer tiledLayer = new ArcGISTiledLayer(tileCache);
      //tiledLayer.getFullExtent();

      // create a basemap with the tiled layer
      Basemap basemap = new Basemap(tiledLayer);
      // create a scene and add a basemap to it
      scene = new ArcGISScene();
      scene.setBasemap(basemap);

      // add the SceneView to the stack pane
      sceneView = new SceneView();
      sceneView.setArcGISScene(scene);
//      stackPane.getChildren().addAll(sceneView);
      // create a control panel
      VBox vBoxControl = new VBox(6);
      vBoxControl.setMaxSize(60, 20);
      vBoxControl.setAlignment(javafx.geometry.Pos.TOP_LEFT);
      vBoxControl.getStyleClass().add("panel-region");

      Button btn = new Button("change");
      
      btn.setOnAction(e -> {
    	  File file = fileChooser.showOpenDialog(stage);
    	  if (file != null) {
    		  openRaster(file);
    	  }
      });
      vBoxControl.getChildren().addAll(btn);
      stackPane.getChildren().addAll(sceneView,vBoxControl);
      stackPane.setAlignment(javafx.geometry.Pos.TOP_LEFT);
      
      List<String> filePaths = new ArrayList<>();
      filePaths.add("D:/EP/Ortho prachuab/DEM_Prachuab.tif");
//      String rasterPath = new File("D:/EP/tha_dem_srtm/ASTGTM2_N17E097_dem.tif").getAbsolutePath();
//      Raster raster = new Raster(rasterPath);
//      
//      RasterLayer rasterLayer = new RasterLayer(raster);
//      rasterLayer.addDoneLoadingListener(() -> {
//    	   if (rasterLayer.getLoadStatus() == LoadStatus.LOADED) {
//    	     // raster layer has loaded
//    		   //rasterLayer.getLoadStatus();
//    		   
//    	   }
//    	   ArcGISRuntimeException err = rasterLayer.getLoadError();
//    	   rasterLayer.getLoadStatus();
//    	 });


      // add base surface for elevation data
      try {
		RasterElevationSource dem = new RasterElevationSource(filePaths);
		  Surface surface = new Surface();
		  surface.getElevationSources().add(dem);
//      surface.getElevationSources().add(new ArcGISTiledElevationSource(LOCAL_ELEVATION_IMAGE_SERVICE));
		  scene.setBaseSurface(surface);
		  List<String> paths = dem.getFilePaths();
		  boolean enable = dem.isEnabled();
		  LoadStatus loadStatus = dem.getLoadStatus();
		  ArcGISRuntimeException agsError = dem.getLoadError();
		  dem.isEnabled();
		  
	      // add graphics overlay(s)
	      GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
	      graphicsOverlay.getSceneProperties().setSurfacePlacement(LayerSceneProperties.SurfacePlacement.ABSOLUTE);
	      sceneView.getGraphicsOverlays().add(graphicsOverlay);
	      
	      // create renderer to handle updating plane rotation using the GPU
	      SimpleRenderer renderer3D = new SimpleRenderer();
	      renderer3D.setRotationType(RotationType.GEOGRAPHIC);
	      Renderer.SceneProperties renderProperties = renderer3D.getSceneProperties();
	      renderProperties.setHeadingExpression("[HEADING]");
	      renderProperties.setPitchExpression("[PITCH]");
	      renderProperties.setRollExpression("[ROLL]");
	      graphicsOverlay.setRenderer(renderer3D);
	      
	      Graphic plane3D = create3DPlane(99.427, 11.332, 170, "./samples-data/bristol/Collada/Bristol.dae");
	      graphicsOverlay.getGraphics().add(plane3D);
	      
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

      // add a scene layer
//      ArcGISSceneLayer sceneLayer = new ArcGISSceneLayer(BUILDINGS);
//      scene.getOperationalLayers().add(sceneLayer);
//      scene.getOperationalLayers().add(rasterLayer);

      // add a camera and initial camera position (Brest, France)
      Camera camera = new Camera(11.33,99.43, 200.0, 10.0, 70, 0.0);
      sceneView.setViewpointCamera(camera);

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }
  private Graphic create3DPlane(double x,double y,double z,String Uri) throws URISyntaxException {

	    // load the plane's 3D model symbol
	    String modelURI = new File(Uri).getAbsolutePath();
	    ModelSceneSymbol plane3DSymbol = new ModelSceneSymbol(modelURI, 4.0);
	    plane3DSymbol.loadAsync();

	    // create the graphic
	    return new Graphic(new Point(x, y, z, WGS84), plane3DSymbol);
	  }
  /**
   * Stops and releases all resources used in application.
   */
  public void openRaster(File file) {
      Raster raster = new Raster(file.getAbsolutePath());
      // create a raster layer
      if (scene.getOperationalLayers().size() == 1) {
    	  scene.getOperationalLayers().remove(0);
      }
      
      RasterLayer rasterLayer = new RasterLayer(raster);
      scene.getOperationalLayers().add(rasterLayer);
//      Viewpoint viewpoint = new Viewpoint(rasterLayer.getFullExtent());

      // set viewpoint on the raster
      rasterLayer.addDoneLoadingListener(() -> sceneView.setViewpointAsync(new Viewpoint(rasterLayer.getFullExtent()), 5));

  }

  @Override
  public void stop() {

    if (sceneView != null) {
      sceneView.dispose();
    }
  }

  /**
   * Opens and runs application.
   *
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {

    Application.launch(args);
  }

}
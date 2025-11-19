package com.teclever.dfcc.buildconfiguration;

import java.util.ArrayList;
import java.util.List;

import com.teclever.datastore.dto.GetObjResponse;
import com.teclever.datastore.dto.Response;
import com.teclever.datastore.entities.BuildConfiguration;
import com.teclever.datastore.service.BuildConfigurationService;
import com.teclever.datastore.utils.GetResponse;

public class BuildConfigurationManagement {

	public Response addBuildConfiguration(String serialNo, String versionName, BuildConfiguration buildConfiguration) {

		Response response = new Response();
		try {
			BuildConfigurationService buildConfigurationService = new BuildConfigurationService();
			response = buildConfigurationService.addingBuildConfiguration(serialNo, versionName, buildConfiguration);

		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
		}
		return response;
	}

	public BuildConfiguration getBuildConfiguration(String serialNo, String versionName) {
		BuildConfiguration buildConfiguration = new BuildConfiguration();
		try {
			BuildConfigurationService buildConfigurationService = new BuildConfigurationService();
			GetObjResponse getObj = buildConfigurationService.getBuildConfiguration(serialNo, versionName);
			buildConfiguration = (BuildConfiguration) getObj.getObject();
		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
		}
		return buildConfiguration;
	}

	public List<String> getVersionList(String serialNo) {
		List<String> versionList = new ArrayList<String>();
		try {
			BuildConfigurationService buildConfigurationService = new BuildConfigurationService();

			GetResponse response = new GetResponse();
			response = buildConfigurationService.getVersionDetailsBySlNo(serialNo);
			List<BuildConfiguration> bulList = (List<BuildConfiguration>) response.getResponseList();

			for (BuildConfiguration buildConfiguration : bulList) {
				versionList.add(buildConfiguration.getVersionName());
			}

		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
		}
		return versionList;
	}

	public boolean isVersionNameAvailble(String serialNo, String versionName) {
		boolean avaible = false;
		try {
			List<String> versionNames = new ArrayList<String>();
			versionNames = getVersionList(serialNo);
			if (versionNames.contains(versionName)) {
				avaible = true;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return avaible;
	}

	public Response deleteBuildConfiguration(String serialNo, String versionName) {
		Response res = new Response();
		try {
			BuildConfigurationService buildConfigurationService = new BuildConfigurationService();
			res = buildConfigurationService.deleteBuildConfiguration(serialNo, versionName);

		} catch (Exception ex) {
			System.out.println(ex.getLocalizedMessage());
		}
		return res;
	}
}

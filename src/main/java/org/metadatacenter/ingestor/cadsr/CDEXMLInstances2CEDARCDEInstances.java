package org.metadatacenter.ingestor.cadsr;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.metadatacenter.ingestor.cedar.*;
import org.metadatacenter.ingestor.cedar.AlternateName;
import org.metadatacenter.ingestor.cedar.AlternateNameType;
import org.metadatacenter.ingestor.cedar.ClassificationSchemeItemName;
import org.metadatacenter.ingestor.cedar.ClassificationSchemeItemType;
import org.metadatacenter.ingestor.cedar.ComponentDataElementsList;
import org.metadatacenter.ingestor.cedar.ConcatenationCharacter;
import org.metadatacenter.ingestor.cedar.ConceptDetails;
import org.metadatacenter.ingestor.cedar.DerivationType;
import org.metadatacenter.ingestor.cedar.DerivationTypeDescription;
import org.metadatacenter.ingestor.cedar.DocumentText;
import org.metadatacenter.ingestor.cedar.DocumentType;
import org.metadatacenter.ingestor.cedar.Language;
import org.metadatacenter.ingestor.cedar.Methods;
import org.metadatacenter.ingestor.cedar.Name;
import org.metadatacenter.ingestor.cedar.OrganizationName;
import org.metadatacenter.ingestor.cedar.PermissibleValues;
import org.metadatacenter.ingestor.cedar.Rule;
import org.metadatacenter.ingestor.cedar.URL;
import org.metadatacenter.ingestor.cedar.ValueDomainConcepts;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class CDEXMLInstances2CEDARCDEInstances {
  public static void main(String[] argc) throws IOException, JAXBException, DatatypeConfigurationException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

    // Create JAXB XML unmarshaller
    JAXBContext jaxbContext = JAXBContext.newInstance(DataElementsList.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();


    boolean temp = false;
    File xmlFile;
    File[] xmlFileList; //how to use file array
    String xmlDirectoryName;
    String jsonDirectoryName = null;
    DataElementsList dataElementsList;
    if (argc.length == 0) {
      temp = true;
      xmlDirectoryName = "src/main/resources/xml/example/";

      // Specify the XML file containing caDSR data elements
      xmlFile = new File(xmlDirectoryName);
      xmlFileList = xmlFile.listFiles();
    } else {
      xmlDirectoryName = argc[0];
      jsonDirectoryName = argc[1];
      File directory = new File(xmlDirectoryName);
      xmlFileList = directory.listFiles();
    }
    if (xmlFileList == null) {
      System.out.println("There are no files in this directory.");
      System.exit(1);
    }
    //check for null, exit if no files to process
    int i = 0;
    int j = 0;
    for(File file : xmlFileList) {
      String filename = file.getName();

      if (file.isFile() && (filename.endsWith(".xml") || filename.endsWith(".XML"))) {
        // Read a list of DataElement objects from an XML file containing a list of CDEs
        dataElementsList = ((DataElementsList) jaxbUnmarshaller.unmarshal(file));

        for (org.metadatacenter.ingestor.cadsr.DataElement cadsrDataElement : dataElementsList.dataElement) {

          org.metadatacenter.ingestor.cedar.DataElement cedarDataElement = convertDataElement(cadsrDataElement);

          // Specify a temporary file to store a DataElement template instance
          File cdeFile;
          if (temp) {
            cdeFile = File.createTempFile("DataElement", ".json");
          } else {
            String jsonFilePath = jsonDirectoryName + String.format("CDE_%d.json", i);
            cdeFile = new File(jsonFilePath);

            boolean bool;
            bool = cdeFile.createNewFile();
            if(bool) {
              System.out.println(String.format("New file created: %s", cdeFile));
            } else {
              System.out.println(String.format("File overwritten: %s", cdeFile));
            }
          }


          // System.out.println("Writing DataElement to " + cdeFile.getAbsolutePath());

          // Serialize the CDE instance
          mapper.writeValue(cdeFile, cedarDataElement);
          i++;
        }
      } else {
        System.out.println(String.format("Invalid file: %s", filename));
        j++;
      }
    }
    for (File print_file : xmlFileList) {
      System.out.println(print_file.toString());
    }
    if (j > 0) {
      System.out.println(String.format("INTS NOT USED: %d", j));
    }

  }

  private static org.metadatacenter.ingestor.cedar.DataElement convertDataElement(DataElement cadsrDataElement) {
    // Process each DataElement
    //System.out.println("Processing DataElement....");

    // Create a DataElement Java object (which we will serialize as a CEDAR template instance) for each DataElement
    org.metadatacenter.ingestor.cedar.DataElement cedarDataElement = new org.metadatacenter.ingestor.cedar.DataElement();

    // Transfer the content of each caDSR DataElement to a CEDAR DataElement
    //System.out.println("list CDE");

    // URI id = URI.create("https://example.com/Dummy");
    //cedarDataElement.setId(id);

    URI templateID = URI.create("https://repo.metadatacenter.net/templates/4373d376-c567-40c9-8349-dfa152908aac");
    //URI templateID = URI.create("https://repo.metadatacenter.net/templates/d2539f1a-d4f2-4e71-8038-ba4ea8cf2877");


    convertCADSR2CEDARDataElementAttributes(cedarDataElement, cadsrDataElement);
    convertCADSR2CEDARDataElementConcept(cedarDataElement, cadsrDataElement);
    convertCADSR2CEDARValueDomain(cedarDataElement, cadsrDataElement);
    convertCADSR2CEDARReferenceDocuments(cedarDataElement, cadsrDataElement);
    convertCADSR2CEDARClassifications(cedarDataElement, cadsrDataElement);
    convertCADSR2CEDARAlternateNames(cedarDataElement, cadsrDataElement);
    convertCADSR2CEDARDataElementDerivation(cedarDataElement, cadsrDataElement);

    cedarDataElement.setSchemaIsBasedOn(templateID);

    return cedarDataElement;
  }


  private static void convertCADSR2CEDARDataElementAttributes(org.metadatacenter.ingestor.cedar.DataElement cedarDataElement, DataElement cadsrDataElement) {

    //data element details
    //System.out.println("**Data Element Details**");

    //RAI
    //all caDSR CDEs come from the same registry and have the same Registration Authority Identifier
    String rai = "2.16.840.1.113883.3.26.2";
    //System.out.println(rai);
    RAI cedarRAI = new RAI();
    cedarRAI.setValue(rai);
    cedarDataElement.setRAI(cedarRAI);

    //DE public ID
    String cadsrPublicID = cadsrDataElement.getPUBLICID().getContent();
    //System.out.println(cadsrPublicID);
    PublicID cedarPublicID = new org.metadatacenter.ingestor.cedar.PublicID();
    cedarPublicID.setValue(cadsrPublicID);
    cedarDataElement.setPublicID(cedarPublicID);

    //DE Long Name
    String cadsrLongName = cadsrDataElement.getLONGNAME().getContent();
    //System.out.println(cadsrLongName);
    org.metadatacenter.ingestor.cedar.LongName cedarLongName = new org.metadatacenter.ingestor.cedar.LongName();
    cedarLongName.setValue(cadsrLongName);
    cedarDataElement.setLongName(cedarLongName);

    //DE Preferred Name
    String cadsrPreferredName = cadsrDataElement.getPREFERREDNAME().getContent();
    //System.out.println(cadsrPreferredName);
    org.metadatacenter.ingestor.cedar.PreferredName cedarPreferredName = new org.metadatacenter.ingestor.cedar.PreferredName();
    cedarPreferredName.setValue(cadsrPreferredName);
    cedarDataElement.setPreferredName(cedarPreferredName);

    //DE Preferred Definition
    String cadsrPreferredDefinition = cadsrDataElement.getPREFERREDDEFINITION().getContent();
    //System.out.println(cadsrPreferredDefinition);
    org.metadatacenter.ingestor.cedar.PreferredDefinition cedarPreferredDefinition = new org.metadatacenter.ingestor.cedar.PreferredDefinition();
    cedarPreferredDefinition.setValue(cadsrPreferredDefinition);
    cedarDataElement.setPreferredDefinition(cedarPreferredDefinition);

    //set Schema Name (and description)
    cedarDataElement.setSchemaName(cadsrPublicID+" : "+cadsrLongName);
    cedarDataElement.setSchemaDescription(cadsrPreferredDefinition);

    //DE Version
    String cadsrVersion = cadsrDataElement.getVERSION().getContent();
    //System.out.println(cadsrVersion);
    org.metadatacenter.ingestor.cedar.Version cedarVersion = new org.metadatacenter.ingestor.cedar.Version();
    cedarVersion.setValue(cadsrVersion);
    cedarDataElement.setVersion(cedarVersion);

    //DE Workflow Status
    String cadsrWorkflowStatus = cadsrDataElement.getWORKFLOWSTATUS().getContent();
    //System.out.println(cadsrWorkflowStatus);
    org.metadatacenter.ingestor.cedar.WorkflowStatus cedarWorkflowStatus = new org.metadatacenter.ingestor.cedar.WorkflowStatus();
    cedarWorkflowStatus.setValue(cadsrWorkflowStatus);
    cedarDataElement.setWorkflowStatus(cedarWorkflowStatus);

    //DE Context Name
    String cadsrContextName = cadsrDataElement.getCONTEXTNAME().getContent();
    //System.out.println(cadsrContextName);
    org.metadatacenter.ingestor.cedar.ContextName cedarContextName = new org.metadatacenter.ingestor.cedar.ContextName();
    cedarContextName.setValue(cadsrContextName);
    cedarDataElement.setContextName(cedarContextName);

    //DE Context Version
    String cadsrContextVersion = cadsrDataElement.getCONTEXTVERSION().getContent();
    //System.out.println(cadsrContextVersion);
    org.metadatacenter.ingestor.cedar.ContextVersion cedarContextVersion = new org.metadatacenter.ingestor.cedar.ContextVersion();
    cedarContextVersion.setValue(cadsrContextVersion);
    cedarDataElement.setContextVersion(cedarContextVersion);

    //DE Origin
    String cadsrOrigin = cadsrDataElement.getORIGIN().getContent();
    if (cadsrOrigin.equals("")) {
      cadsrOrigin = cadsrDataElement.getORIGIN().getNULL();
      if (cadsrOrigin.equals("TRUE")) {
        cadsrOrigin = "NULL";
      }
    }
    //System.out.println(cadsrOrigin);
    org.metadatacenter.ingestor.cedar.Origin cedarOrigin = new org.metadatacenter.ingestor.cedar.Origin();
    cedarOrigin.setValue(cadsrOrigin);
    cedarDataElement.setOrigin(cedarOrigin);

    //DE Registration Status
    String cadsrRegistrationStatus = cadsrDataElement.getREGISTRATIONSTATUS().getContent();
    //System.out.println(cadsrRegistrationStatus);
    org.metadatacenter.ingestor.cedar.RegistrationStatus cedarRegistrationStatus = new org.metadatacenter.ingestor.cedar.RegistrationStatus();
    cedarRegistrationStatus.setValue(cadsrRegistrationStatus);
    cedarDataElement.setRegistrationStatus(cedarRegistrationStatus);
  }



  private static void convertCADSR2CEDARDataElementConcept(org.metadatacenter.ingestor.cedar.DataElement cedarDataElement, DataElement cadsrDataElement) {
    // build Data element concept from xml to json
    //System.out.println("**Data Element Concept**");
    DATAELEMENTCONCEPT cadsrDATAELEMENTCONCEPT = cadsrDataElement.getDATAELEMENTCONCEPT();
    DataElementConcept cedarDataElementConcept = new DataElementConcept();
    // URI id = URI.create("https://example.com/Dummy");
    //cedarDataElementConcept.setId(id);

    //DEC Public ID
    String cadsrDECPublicID = cadsrDATAELEMENTCONCEPT.getPublicId().getContent();
    //System.out.println(cadsrDECPublicID);
    PublicID_ cedarDECPublicID = new PublicID_();
    cedarDECPublicID.setValue(cadsrDECPublicID);
    cedarDataElementConcept.setPublicID(cedarDECPublicID);

    //DEC Preferred Name
    String cadsrDECPreferredName = cadsrDATAELEMENTCONCEPT.getPreferredName().getContent();
    //System.out.println(cadsrDECPreferredName);
    org.metadatacenter.ingestor.cedar.PreferredName_ cedarDECPreferredName = new PreferredName_();
    cedarDECPreferredName.setValue(cadsrDECPreferredName);
    cedarDataElementConcept.setPreferredName(cedarDECPreferredName);


    //DEC Preferred Definition
    String cadsrDECPreferredDefinition = cadsrDATAELEMENTCONCEPT.getPreferredDefinition().getContent();
    //System.out.println(cadsrDECPreferredDefinition);
    org.metadatacenter.ingestor.cedar.PreferredDefinition_ cedarDECPreferredDefinition = new PreferredDefinition_();
    cedarDECPreferredDefinition.setValue(cadsrDECPreferredDefinition);
    cedarDataElementConcept.setPreferredDefinition(cedarDECPreferredDefinition);

    //DEC Long Name
    String cadsrDECLongName = cadsrDATAELEMENTCONCEPT.getLongName().getContent();
    //System.out.println(cadsrDECLongName);
    org.metadatacenter.ingestor.cedar.LongName_ cedarDECLongName = new LongName_();
    cedarDECLongName.setValue(cadsrDECLongName);
    cedarDataElementConcept.setLongName(cedarDECLongName);

    //DEC Version
    String cadsrDECVersion = cadsrDATAELEMENTCONCEPT.getVersion().getContent();
    //System.out.println(cadsrDECVersion);
    org.metadatacenter.ingestor.cedar.Version_ cedarDECVersion = new Version_();
    cedarDECVersion.setValue(cadsrDECVersion);
    cedarDataElementConcept.setVersion(cedarDECVersion);

    //DEC Workflow Status
    String cadsrDECWorkflowStatus = cadsrDATAELEMENTCONCEPT.getWorkflowStatus().getContent();
    //System.out.println(cadsrDECWorkflowStatus);
    org.metadatacenter.ingestor.cedar.WorkflowStatus_ cedarDECWorkflowStatus = new WorkflowStatus_();
    cedarDECWorkflowStatus.setValue(cadsrDECWorkflowStatus);
    cedarDataElementConcept.setWorkflowStatus(cedarDECWorkflowStatus);

    //DEC Context Name
    String cadsrDECContextName = cadsrDATAELEMENTCONCEPT.getContextName().getContent();
    //System.out.println(cadsrDECContextName);
    org.metadatacenter.ingestor.cedar.ContextName_ cedarDECContextName = new ContextName_();
    cedarDECContextName.setValue(cadsrDECContextName);
    cedarDataElementConcept.setContextName(cedarDECContextName);

    //DEC Context Version
    String cadsrDECContextVersion = cadsrDATAELEMENTCONCEPT.getContextVersion().getContent();
    //System.out.println(cadsrDECContextVersion);
    org.metadatacenter.ingestor.cedar.ContextVersion_ cedarDECContextVersion = new ContextVersion_();
    cedarDECContextVersion.setValue(cadsrDECContextVersion);
    cedarDataElementConcept.setContextVersion(cedarDECContextVersion);



    convertCADSR2CEDARDataElementConceptConceptualDomain(cedarDataElementConcept, cadsrDATAELEMENTCONCEPT);
    convertCADSR2CEDARObjectClass(cedarDataElementConcept, cadsrDATAELEMENTCONCEPT);
    convertCADSR2CEDARProperty(cedarDataElementConcept, cadsrDATAELEMENTCONCEPT);

    convertCADSR2CEDARObjectClassQualifier(cedarDataElementConcept, cadsrDATAELEMENTCONCEPT);

    convertCADSR2CEDARPropertyQualifier(cedarDataElementConcept, cadsrDATAELEMENTCONCEPT);

    convertCADSR2CEDAROrigin(cedarDataElementConcept, cadsrDATAELEMENTCONCEPT);

    // wire cedar DEC to data element
    //TODO check DEC is complete
    cedarDataElement.setDataElementConcept(cedarDataElementConcept);


  }

  private static void convertCADSR2CEDARDataElementConceptConceptualDomain(DataElementConcept cedarDataElementConcept, DATAELEMENTCONCEPT cadsrDATAELEMENTCONCEPT) {
    //DEC conceptual domain
    //System.out.println("**DEC Conceptual Domain**");
    ConceptualDomain cadsrDATAELEMENTCONCEPTDECConceptualDomain = cadsrDATAELEMENTCONCEPT.getConceptualDomain();
    org.metadatacenter.ingestor.cedar.ConceptualDomain cedarDECConceptualDomain = new org.metadatacenter.ingestor.cedar.ConceptualDomain();
    // URI id = URI.create("https://example.com/Dummy");
    //cedarDECConceptualDomain.setId(id);

    //DEC CD public id
    String cadsrDECcdPublicID = cadsrDATAELEMENTCONCEPTDECConceptualDomain.getPublicId().getContent();
    //System.out.println(cadsrDECcdPublicID);
    org.metadatacenter.ingestor.cedar.PublicID__ cedarDECcdPublicID = new org.metadatacenter.ingestor.cedar.PublicID__();
    cedarDECcdPublicID.setValue(cadsrDECcdPublicID);
    cedarDECConceptualDomain.setPublicID(cedarDECcdPublicID);

    //DEC CD context name
    String cadsrDECcdContextName = cadsrDATAELEMENTCONCEPTDECConceptualDomain.getContextName().getContent();
    //System.out.println(cadsrDECcdContextName);
    org.metadatacenter.ingestor.cedar.ContextName__ cedarDECcdContextName = new org.metadatacenter.ingestor.cedar.ContextName__();
    cedarDECcdContextName.setValue(cadsrDECcdContextName);
    cedarDECConceptualDomain.setContextName(cedarDECcdContextName);

    //DEC CD context version
    String cadsrDECcdContextVersion = cadsrDATAELEMENTCONCEPTDECConceptualDomain.getContextVersion().getContent();
    //System.out.println(cadsrDECcdContextVersion);
    org.metadatacenter.ingestor.cedar.ContextVersion__ cedarDECcdContextVersion = new org.metadatacenter.ingestor.cedar.ContextVersion__();
    cedarDECcdContextVersion.setValue(cadsrDECcdContextVersion);
    cedarDECConceptualDomain.setContextVersion(cedarDECcdContextVersion);

    //DEC CD preferred name
    String cadsrDECcdPreferredName = cadsrDATAELEMENTCONCEPTDECConceptualDomain.getPreferredName().getContent();
    //System.out.println(cadsrDECcdPreferredName);
    org.metadatacenter.ingestor.cedar.PreferredName__ cedarDECcdPreferredName = new org.metadatacenter.ingestor.cedar.PreferredName__();
    cedarDECcdPreferredName.setValue(cadsrDECcdPreferredName);
    cedarDECConceptualDomain.setPreferredName(cedarDECcdPreferredName);

    //DEC CD version
    String cadsrDECcdVersion = cadsrDATAELEMENTCONCEPTDECConceptualDomain.getVersion().getContent();
    //System.out.println(cadsrDECcdVersion);
    org.metadatacenter.ingestor.cedar.Version__ cedarDECcdVersion = new org.metadatacenter.ingestor.cedar.Version__();
    cedarDECcdVersion.setValue(cadsrDECcdVersion);
    cedarDECConceptualDomain.setVersion(cedarDECcdVersion);

    //DEC CD long name
    String cadsrDECcdLongName = cadsrDATAELEMENTCONCEPTDECConceptualDomain.getLongName().getContent();
    //System.out.println(cadsrDECcdLongName);
    org.metadatacenter.ingestor.cedar.LongName__ cedarDECcdLongName = new org.metadatacenter.ingestor.cedar.LongName__();
    cedarDECcdLongName.setValue(cadsrDECcdLongName);
    cedarDECConceptualDomain.setLongName(cedarDECcdLongName);


    //wire DEC conceptual domain to DEC
    cedarDataElementConcept.setConceptualDomain(cedarDECConceptualDomain);
  }

  private static void convertCADSR2CEDARObjectClass(DataElementConcept cedarDataElementConcept, DATAELEMENTCONCEPT cadsrDATAELEMENTCONCEPT) {
    //object class
    //System.out.println("**ObjectClass**");
    ObjectClass cadsrDATAELEMENTCONCEPTObjectClass = cadsrDATAELEMENTCONCEPT.getObjectClass();
    org.metadatacenter.ingestor.cedar.ObjectClass cedarObjectClass = new org.metadatacenter.ingestor.cedar.ObjectClass();
    // URI id = URI.create("https://example.com/Dummy");
    //cedarObjectClass.setId(id);

    //object class public id
    String cadsrObjClassPublicID = cadsrDATAELEMENTCONCEPTObjectClass.getPublicId().getContent();
    //System.out.println(cadsrObjClassPublicID);
    org.metadatacenter.ingestor.cedar.PublicID___ cedarObjClassPublicID = new org.metadatacenter.ingestor.cedar.PublicID___();
    cedarObjClassPublicID.setValue(cadsrObjClassPublicID);
    cedarObjectClass.setPublicID(cedarObjClassPublicID);

    //object class context name
    String cadsrObjClassContextName = cadsrDATAELEMENTCONCEPTObjectClass.getContextName().getContent();
    //System.out.println(cadsrObjClassContextName);
    org.metadatacenter.ingestor.cedar.ContextName___ cedarObjClassContextName = new org.metadatacenter.ingestor.cedar.ContextName___();
    cedarObjClassContextName.setValue(cadsrObjClassContextName);
    cedarObjectClass.setContextName(cedarObjClassContextName);

    //object class context version
    String cadsrObjClassContextVersion = cadsrDATAELEMENTCONCEPTObjectClass.getContextVersion().getContent();
    //System.out.println(cadsrObjClassContextVersion);
    org.metadatacenter.ingestor.cedar.ContextVersion___ cedarObjClassContextVersion = new org.metadatacenter.ingestor.cedar.ContextVersion___();
    cedarObjClassContextVersion.setValue(cadsrObjClassContextVersion);
    cedarObjectClass.setContextVersion(cedarObjClassContextVersion);

    //object class preferred name
    String cadsrObjClassPreferredName = cadsrDATAELEMENTCONCEPTObjectClass.getPreferredName().getContent();
    //System.out.println(cadsrObjClassPreferredName);
    org.metadatacenter.ingestor.cedar.PreferredName___ cedarObjClassPreferredName = new org.metadatacenter.ingestor.cedar.PreferredName___();
    cedarObjClassPreferredName.setValue(cadsrObjClassPreferredName);
    cedarObjectClass.setPreferredName(cedarObjClassPreferredName);

    //object class version
    String cadsrObjClassVersion = cadsrDATAELEMENTCONCEPTObjectClass.getVersion().getContent();
    //System.out.println(cadsrObjClassVersion);
    org.metadatacenter.ingestor.cedar.Version___ cedarObjClassVersion = new org.metadatacenter.ingestor.cedar.Version___();
    cedarObjClassVersion.setValue(cadsrObjClassVersion);
    cedarObjectClass.setVersion(cedarObjClassVersion);

    //object class long name
    String cadsrObjClassLongName = cadsrDATAELEMENTCONCEPTObjectClass.getLongName().getContent();
    //System.out.println(cadsrObjClassLongName);
    org.metadatacenter.ingestor.cedar.LongName___ cedarObjClassLongName = new org.metadatacenter.ingestor.cedar.LongName___();
    cedarObjClassLongName.setValue(cadsrObjClassLongName);
    cedarObjectClass.setLongName(cedarObjClassLongName);


    convertCADSR2CEDARObjectClassConceptDetails(cedarDataElementConcept, cedarObjectClass, cadsrDATAELEMENTCONCEPTObjectClass);
  }

  private static void convertCADSR2CEDARObjectClassConceptDetails(DataElementConcept cedarDataElementConcept, org.metadatacenter.ingestor.cedar.ObjectClass cedarObjectClass, ObjectClass cadsrDATAELEMENTCONCEPTObjectClass) {
    //object class concept details list
    List<ConceptDetailsITEM> cadsrDATAELEMENTCONCEPTObjectClassConceptDetailsITEM = cadsrDATAELEMENTCONCEPTObjectClass.getConceptDetails().getConceptDetailsITEM();
    org.metadatacenter.ingestor.cedar.ConceptDetails cedarObjClassConceptDetails = new ConceptDetails();
    // URI id = URI.create("https://example.com/Dummy");
    //cedarObjClassConceptDetails.setId(id);

    List<ConceptDetailsItem> cedarObjClassConceptDetailsList = new ArrayList<ConceptDetailsItem>();

    if (!cadsrDATAELEMENTCONCEPTObjectClassConceptDetailsITEM.isEmpty()) {
      for (ConceptDetailsITEM val : cadsrDATAELEMENTCONCEPTObjectClassConceptDetailsITEM) {
        org.metadatacenter.ingestor.cedar.ConceptDetailsItem cedarObjClassConceptDetailsItem = new ConceptDetailsItem();
        //cedarObjClassConceptDetailsItem.setId(id);

        //System.out.println("object class concept details list item: ");

        //obj class concept details preferred name
        String cadsrObjClassConceptDetailsItemPreferredName = val.getPREFERREDNAME().getContent();
        //System.out.println(cadsrObjClassConceptDetailsItemPreferredName);
        org.metadatacenter.ingestor.cedar.PreferredName____ cedarObjClassConceptDetailsItemPreferredName = new PreferredName____();
        cedarObjClassConceptDetailsItemPreferredName.setValue(cadsrObjClassConceptDetailsItemPreferredName);
        cedarObjClassConceptDetailsItem.setPreferredName(cedarObjClassConceptDetailsItemPreferredName);

        //obj class concept details long name
        String cadsrObjClassConceptDetailsItemLongName = val.getLONGNAME().getContent();
        //System.out.println(cadsrObjClassConceptDetailsItemLongName);
        org.metadatacenter.ingestor.cedar.LongName____ cedarObjClassConceptDetailsItemLongName = new LongName____();
        cedarObjClassConceptDetailsItemLongName.setValue(cadsrObjClassConceptDetailsItemLongName);
        cedarObjClassConceptDetailsItem.setLongName(cedarObjClassConceptDetailsItemLongName);

        //obj class concept details concept id
        String cadsrObjClassConceptDetailsItemConID = val.getCONID().getContent();
        //System.out.println(cadsrObjClassConceptDetailsItemConID);
        org.metadatacenter.ingestor.cedar.ConceptID cedarObjClassConceptDetailsItemConID = new ConceptID();
        cedarObjClassConceptDetailsItemConID.setValue(cadsrObjClassConceptDetailsItemConID);
        cedarObjClassConceptDetailsItem.setConceptID(cedarObjClassConceptDetailsItemConID);

        //obj class concept details definition source
        String cadsrObjClassConceptDetailsItemDefSource = val.getDEFINITIONSOURCE().getContent();
        //System.out.println(cadsrObjClassConceptDetailsItemDefSource);
        org.metadatacenter.ingestor.cedar.DefinitionSource cedarObjClassConceptDetailsItemDefSource = new DefinitionSource();
        cedarObjClassConceptDetailsItemDefSource.setValue(cadsrObjClassConceptDetailsItemDefSource);
        cedarObjClassConceptDetailsItem.setDefinitionSource(cedarObjClassConceptDetailsItemDefSource);

        //obj class concept details origin
        String cadsrObjClassConceptDetailsItemOrigin = val.getORIGIN().getContent();
        //System.out.println(cadsrObjClassConceptDetailsItemOrigin);
        org.metadatacenter.ingestor.cedar.Origin_ cedarObjClassConceptDetailsItemOrigin = new Origin_();
        cedarObjClassConceptDetailsItemOrigin.setValue(cadsrObjClassConceptDetailsItemOrigin);
        cedarObjClassConceptDetailsItem.setOrigin(cedarObjClassConceptDetailsItemOrigin);

        //obj class concept details EVS Source
        String cadsrObjClassConceptDetailsItemEVS = val.getEVSSOURCE().getContent();
        //System.out.println(cadsrObjClassConceptDetailsItemEVS);
        org.metadatacenter.ingestor.cedar.EVSSource cedarObjClassConceptDetailsItemEVS = new EVSSource();
        cedarObjClassConceptDetailsItemEVS.setValue(cadsrObjClassConceptDetailsItemEVS);
        cedarObjClassConceptDetailsItem.setEVSSource(cedarObjClassConceptDetailsItemEVS);

        //obj class concept details primary flag indicator
        String cadsrObjClassConceptDetailsItemPrimaryFlag = val.getPRIMARYFLAGIND().getContent();
        //System.out.println(cadsrObjClassConceptDetailsItemPrimaryFlag);
        org.metadatacenter.ingestor.cedar.PrimaryFlag cedarObjClassConceptDetailsItemPrimaryFlag = new PrimaryFlag();
        cedarObjClassConceptDetailsItemPrimaryFlag.setValue(cadsrObjClassConceptDetailsItemPrimaryFlag);
        cedarObjClassConceptDetailsItem.setPrimaryFlag(cedarObjClassConceptDetailsItemPrimaryFlag);

        //obj class concept details display order
        String cadsrObjClassConceptDetailsItemDisplayOrder = val.getDISPLAYORDER().getContent();
        //System.out.println(cadsrObjClassConceptDetailsItemDisplayOrder);
        org.metadatacenter.ingestor.cedar.DisplayOrder cedarObjClassConceptDetailsItemDisplayOrder = new DisplayOrder();
        cedarObjClassConceptDetailsItemDisplayOrder.setValue(cadsrObjClassConceptDetailsItemDisplayOrder);
        cedarObjClassConceptDetailsItem.setDisplayOrder(cedarObjClassConceptDetailsItemDisplayOrder);

        cedarObjClassConceptDetailsList.add(cedarObjClassConceptDetailsItem);
      }
    }

    cedarObjClassConceptDetails.setConceptDetailsItem(cedarObjClassConceptDetailsList);

    cedarObjectClass.setConceptDetails(cedarObjClassConceptDetails);

    //TODO figure out empty case (null?)

    cedarDataElementConcept.setObjectClass(cedarObjectClass);
  }

  private static void convertCADSR2CEDARProperty(DataElementConcept cedarDataElementConcept, DATAELEMENTCONCEPT cadsrDATAELEMENTCONCEPT) {
    //property
    //System.out.println("**Property**");
    Property cadsrDATAELEMENTCONCEPTProperty = cadsrDATAELEMENTCONCEPT.getProperty();

    org.metadatacenter.ingestor.cedar.Property cedarProperty = new org.metadatacenter.ingestor.cedar.Property();
    // URI id = URI.create("https://example.com/Dummy");
    //cedarProperty.setId(id);

    //property public id
    String cadsrPropertyPublicID = cadsrDATAELEMENTCONCEPTProperty.getPublicId().getContent();
    //System.out.println(cadsrPropertyPublicID);
    org.metadatacenter.ingestor.cedar.PublicID____ cedarPropertyPublicID = new org.metadatacenter.ingestor.cedar.PublicID____();
    cedarPropertyPublicID.setValue(cadsrPropertyPublicID);
    cedarProperty.setPublicID(cedarPropertyPublicID);

    //property context name
    String cadsrPropertyContextName = cadsrDATAELEMENTCONCEPTProperty.getContextName().getContent();
    //System.out.println(cadsrPropertyContextName);
    org.metadatacenter.ingestor.cedar.ContextName____ cedarPropertyContextName = new org.metadatacenter.ingestor.cedar.ContextName____();
    cedarPropertyContextName.setValue(cadsrPropertyContextName);
    cedarProperty.setContextName(cedarPropertyContextName);

    //property context version
    String cadsrPropertyContextVersion = cadsrDATAELEMENTCONCEPTProperty.getContextVersion().getContent();
    //System.out.println(cadsrPropertyContextVersion);
    org.metadatacenter.ingestor.cedar.ContextVersion____ cedarPropertyContextVersion = new org.metadatacenter.ingestor.cedar.ContextVersion____();
    cedarPropertyContextVersion.setValue(cadsrPropertyContextVersion);
    cedarProperty.setContextVersion(cedarPropertyContextVersion);

    //property preferred name
    String cadsrPropertyPreferredName = cadsrDATAELEMENTCONCEPTProperty.getPreferredName().getContent();
    //System.out.println(cadsrPropertyPreferredName);
    org.metadatacenter.ingestor.cedar.PreferredName_____ cedarPropertyPreferredName = new org.metadatacenter.ingestor.cedar.PreferredName_____();
    cedarPropertyPreferredName.setValue(cadsrPropertyPreferredName);
    cedarProperty.setPreferredName(cedarPropertyPreferredName);

    //property version
    String cadsrPropertyVersion = cadsrDATAELEMENTCONCEPTProperty.getVersion().getContent();
    //System.out.println(cadsrPropertyVersion);
    org.metadatacenter.ingestor.cedar.Version____ cedarPropertyVersion = new org.metadatacenter.ingestor.cedar.Version____();
    cedarPropertyVersion.setValue(cadsrPropertyVersion);
    cedarProperty.setVersion(cedarPropertyVersion);

    //property long name
    String cadsrPropertyLongName = cadsrDATAELEMENTCONCEPTProperty.getLongName().getContent();
    //System.out.println(cadsrPropertyLongName);
    org.metadatacenter.ingestor.cedar.LongName_____ cedarPropertyLongName = new org.metadatacenter.ingestor.cedar.LongName_____();
    cedarPropertyLongName.setValue(cadsrPropertyLongName);
    cedarProperty.setLongName(cedarPropertyLongName);

    convertCADSR2CEDARPropertyConceptDetails(cedarDataElementConcept, cedarProperty, cadsrDATAELEMENTCONCEPTProperty);
  }

  private static void convertCADSR2CEDARPropertyConceptDetails(DataElementConcept cedarDataElementConcept, org.metadatacenter.ingestor.cedar.Property cedarProperty, Property cadsrDATAELEMENTCONCEPTProperty) {
    //property concept details list
    List<ConceptDetailsITEM> cadsrDATAELEMENTCONCEPTPropertyConceptDetailsITEM = cadsrDATAELEMENTCONCEPTProperty.getConceptDetails().getConceptDetailsITEM();
    org.metadatacenter.ingestor.cedar.ConceptDetails_ cedarPropertyConceptDetails = new ConceptDetails_();
    // URI id = URI.create("https://example.com/Dummy");
    //cedarPropertyConceptDetails.setId(id);

    List<ConceptDetailsItem_> cedarPropertyConceptDetailsList = new ArrayList<ConceptDetailsItem_>();

    if (!cadsrDATAELEMENTCONCEPTPropertyConceptDetailsITEM.isEmpty()) {
      for (ConceptDetailsITEM val : cadsrDATAELEMENTCONCEPTPropertyConceptDetailsITEM) {
        org.metadatacenter.ingestor.cedar.ConceptDetailsItem_ cedarPropertyConceptDetailsItem = new ConceptDetailsItem_();
        //cedarPropertyConceptDetailsItem.setId(id);

        //System.out.println("property concept details list item: ");

        //property concept details preferred name
        String cadsrPropertyConceptDetailsItemPreferredName = val.getPREFERREDNAME().getContent();
        //System.out.println(cadsrPropertyConceptDetailsItemPreferredName);
        org.metadatacenter.ingestor.cedar.PreferredName______ cedarPropertyConceptDetailsItemPreferredName = new PreferredName______();
        cedarPropertyConceptDetailsItemPreferredName.setValue(cadsrPropertyConceptDetailsItemPreferredName);
        cedarPropertyConceptDetailsItem.setPreferredName(cedarPropertyConceptDetailsItemPreferredName);

        //property concept details long name
        String cadsrPropertyConceptDetailsItemLongName = val.getLONGNAME().getContent();
        //System.out.println(cadsrPropertyConceptDetailsItemLongName);
        org.metadatacenter.ingestor.cedar.LongName______ cedarPropertyConceptDetailsItemLongName = new LongName______();
        cedarPropertyConceptDetailsItemLongName.setValue(cadsrPropertyConceptDetailsItemLongName);
        cedarPropertyConceptDetailsItem.setLongName(cedarPropertyConceptDetailsItemLongName);

        //property concept details concept id
        String cadsrPropertyConceptDetailsItemConID = val.getCONID().getContent();
        //System.out.println(cadsrPropertyConceptDetailsItemConID);
        org.metadatacenter.ingestor.cedar.ConceptID_ cedarPropertyConceptDetailsItemConID = new ConceptID_();
        cedarPropertyConceptDetailsItemConID.setValue(cadsrPropertyConceptDetailsItemConID);
        cedarPropertyConceptDetailsItem.setConceptID(cedarPropertyConceptDetailsItemConID);

        //property concept details definition source
        String cadsrPropertyConceptDetailsItemDefSource = val.getDEFINITIONSOURCE().getContent();
        //System.out.println(cadsrPropertyConceptDetailsItemDefSource);
        org.metadatacenter.ingestor.cedar.DefinitionSource_ cedarPropertyConceptDetailsItemDefSource = new DefinitionSource_();
        cedarPropertyConceptDetailsItemDefSource.setValue(cadsrPropertyConceptDetailsItemDefSource);
        cedarPropertyConceptDetailsItem.setDefinitionSource(cedarPropertyConceptDetailsItemDefSource);

        //property concept details origin
        String cadsrPropertyConceptDetailsItemOrigin = val.getORIGIN().getContent();
        //System.out.println(cadsrPropertyConceptDetailsItemOrigin);
        org.metadatacenter.ingestor.cedar.Origin__ cedarPropertyConceptDetailsItemOrigin = new Origin__();
        cedarPropertyConceptDetailsItemOrigin.setValue(cadsrPropertyConceptDetailsItemOrigin);
        cedarPropertyConceptDetailsItem.setOrigin(cedarPropertyConceptDetailsItemOrigin);

        //property concept details EVS Source
        String cadsrPropertyConceptDetailsItemEVS = val.getEVSSOURCE().getContent();
        //System.out.println(cadsrPropertyConceptDetailsItemEVS);
        org.metadatacenter.ingestor.cedar.EVSSource_ cedarPropertyConceptDetailsItemEVS = new EVSSource_();
        cedarPropertyConceptDetailsItemEVS.setValue(cadsrPropertyConceptDetailsItemEVS);
        cedarPropertyConceptDetailsItem.setEVSSource(cedarPropertyConceptDetailsItemEVS);

        //property concept details primary flag indicator
        String cadsrPropertyConceptDetailsItemPrimaryFlag = val.getPRIMARYFLAGIND().getContent();
        //System.out.println(cadsrPropertyConceptDetailsItemPrimaryFlag);
        org.metadatacenter.ingestor.cedar.PrimaryFlag_ cedarPropertyConceptDetailsItemPrimaryFlag = new PrimaryFlag_();
        cedarPropertyConceptDetailsItemPrimaryFlag.setValue(cadsrPropertyConceptDetailsItemPrimaryFlag);
        cedarPropertyConceptDetailsItem.setPrimaryFlag(cedarPropertyConceptDetailsItemPrimaryFlag);

        //property concept details display order
        String cadsrPropertyConceptDetailsItemDisplayOrder = val.getDISPLAYORDER().getContent();
        //System.out.println(cadsrPropertyConceptDetailsItemDisplayOrder);
        org.metadatacenter.ingestor.cedar.DisplayOrder_ cedarPropertyConceptDetailsItemDisplayOrder = new DisplayOrder_();
        cedarPropertyConceptDetailsItemDisplayOrder.setValue(cadsrPropertyConceptDetailsItemDisplayOrder);
        cedarPropertyConceptDetailsItem.setDisplayOrder(cedarPropertyConceptDetailsItemDisplayOrder);

        cedarPropertyConceptDetailsList.add(cedarPropertyConceptDetailsItem);

      }
    }

    cedarPropertyConceptDetails.setConceptDetailsItem(cedarPropertyConceptDetailsList);

    cedarProperty.setConceptDetails(cedarPropertyConceptDetails);

    cedarDataElementConcept.setProperty(cedarProperty);
  }

  private static void convertCADSR2CEDARObjectClassQualifier(DataElementConcept cedarDataElementConcept, DATAELEMENTCONCEPT cadsrDATAELEMENTCONCEPT) {
    //object class qualifier
    String cadsrObjectClassQualifier = cadsrDATAELEMENTCONCEPT.getObjectClassQualifier().getContent();
    org.metadatacenter.ingestor.cedar.ObjectClassQualifier cedarObjectClassQualifier = new org.metadatacenter.ingestor.cedar.ObjectClassQualifier();

    if (cadsrObjectClassQualifier.equals("")) {
      cadsrObjectClassQualifier = cadsrDATAELEMENTCONCEPT.getObjectClassQualifier().getNULL();
      if (cadsrObjectClassQualifier.equals("TRUE")) {
        cadsrObjectClassQualifier = "NULL";
      }
    }
    //System.out.println(cadsrObjectClassQualifier);
    cedarObjectClassQualifier.setValue(cadsrObjectClassQualifier);
    cedarDataElementConcept.setObjectClassQualifier(cedarObjectClassQualifier);
  }

  private static void convertCADSR2CEDARPropertyQualifier(DataElementConcept cedarDataElementConcept, DATAELEMENTCONCEPT cadsrDATAELEMENTCONCEPT) {
    //property qualifier
    String cadsrPropertyQualifier = cadsrDATAELEMENTCONCEPT.getPropertyQualifier().getContent();
    org.metadatacenter.ingestor.cedar.PropertyQualifier cedarPropertyQualifier = new org.metadatacenter.ingestor.cedar.PropertyQualifier();

    if (cadsrPropertyQualifier.equals("")) { //TODO does this work instead of ".isEmpty()"?
      cadsrPropertyQualifier = cadsrDATAELEMENTCONCEPT.getPropertyQualifier().getNULL();
      if (cadsrPropertyQualifier.equals("TRUE")) {
        cadsrPropertyQualifier = "NULL";
      }
    }
    //System.out.println(cadsrPropertyQualifier);
    cedarPropertyQualifier.setValue(cadsrPropertyQualifier);
    cedarDataElementConcept.setPropertyQualifier(cedarPropertyQualifier);
  }

  private static void convertCADSR2CEDAROrigin(DataElementConcept cedarDataElementConcept, DATAELEMENTCONCEPT cadsrDATAELEMENTCONCEPT) {
    //origin
    String cadsrDECOrigin = cadsrDATAELEMENTCONCEPT.getOrigin().getContent();
    org.metadatacenter.ingestor.cedar.Origin___ cedarDECOrigin = new org.metadatacenter.ingestor.cedar.Origin___();

    if (cadsrDECOrigin.equals("")) {
      cadsrDECOrigin = cadsrDATAELEMENTCONCEPT.getOrigin().getNULL();
      if (cadsrDECOrigin.equals("TRUE")) {
        cadsrDECOrigin = "NULL";
      }
    }
    //System.out.println(cadsrDECOrigin);
    cedarDECOrigin.setValue(cadsrDECOrigin);
    cedarDataElementConcept.setOrigin(cedarDECOrigin);
  }

  private static void convertCADSR2CEDARValueDomain(org.metadatacenter.ingestor.cedar.DataElement cedarDataElement, DataElement cadsrDataElement) {
    //value domain
    //System.out.println("**Value Domain**");
    VALUEDOMAIN cadsrVALUEDOMAIN = cadsrDataElement.getVALUEDOMAIN();
    org.metadatacenter.ingestor.cedar.ValueDomain cedarValueDomain = new org.metadatacenter.ingestor.cedar.ValueDomain();
    // URI id = URI.create("https://example.com/Dummy");
    //cedarValueDomain.setId(id);

    //value domain public id
    String cadsrValueDomainPublicID = cadsrVALUEDOMAIN.getPublicId().getContent();
    //System.out.println(cadsrValueDomainPublicID);
    org.metadatacenter.ingestor.cedar.PublicID_____ cedarValueDomainPublicID = new org.metadatacenter.ingestor.cedar.PublicID_____();
    cedarValueDomainPublicID.setValue(cadsrValueDomainPublicID);
    cedarValueDomain.setPublicID(cedarValueDomainPublicID);

    //value domain preferred name
    String cadsrValueDomainPreferredName = cadsrVALUEDOMAIN.getPreferredName().getContent();
    //System.out.println(cadsrValueDomainPreferredName);
    org.metadatacenter.ingestor.cedar.PreferredName_______ cedarValueDomainPreferredName = new org.metadatacenter.ingestor.cedar.PreferredName_______();
    cedarValueDomainPreferredName.setValue(cadsrValueDomainPreferredName);
    cedarValueDomain.setPreferredName(cedarValueDomainPreferredName);

    //value domain preferred definition
    String cadsrValueDomainPreferredDefinition = cadsrVALUEDOMAIN.getPreferredDefinition().getContent();
    //System.out.println(cadsrValueDomainPreferredDefinition);
    org.metadatacenter.ingestor.cedar.PreferredDefinition__ cedarValueDomainPreferredDefinition = new org.metadatacenter.ingestor.cedar.PreferredDefinition__();
    cedarValueDomainPreferredDefinition.setValue(cadsrValueDomainPreferredDefinition);
    cedarValueDomain.setPreferredDefinition(cedarValueDomainPreferredDefinition);

    //value domain long name
    String cadsrValueDomainLongName = cadsrVALUEDOMAIN.getLongName().getContent();
    //System.out.println(cadsrValueDomainLongName);
    org.metadatacenter.ingestor.cedar.LongName_______ cedarValueDomainLongName = new org.metadatacenter.ingestor.cedar.LongName_______();
    cedarValueDomainLongName.setValue(cadsrValueDomainLongName);
    cedarValueDomain.setLongName(cedarValueDomainLongName);

    //value domain version
    String cadsrValueDomainVersion = cadsrVALUEDOMAIN.getVersion().getContent();
    //System.out.println(cadsrValueDomainVersion);
    org.metadatacenter.ingestor.cedar.Version_____ cedarValueDomainVersion = new org.metadatacenter.ingestor.cedar.Version_____();
    cedarValueDomainVersion.setValue(cadsrValueDomainVersion);
    cedarValueDomain.setVersion(cedarValueDomainVersion);

    //value domain workflow status
    String cadsrValueDomainWorkflowStatus = cadsrVALUEDOMAIN.getWorkflowStatus().getContent();
    //System.out.println(cadsrValueDomainWorkflowStatus);
    org.metadatacenter.ingestor.cedar.WorkflowStatus__ cedarValueDomainWorkflowStatus = new org.metadatacenter.ingestor.cedar.WorkflowStatus__();
    cedarValueDomainWorkflowStatus.setValue(cadsrValueDomainWorkflowStatus);
    cedarValueDomain.setWorkflowStatus(cedarValueDomainWorkflowStatus);

    //value domain context name
    String cadsrValueDomainContextName = cadsrVALUEDOMAIN.getContextName().getContent();
    //System.out.println(cadsrValueDomainContextName);
    org.metadatacenter.ingestor.cedar.ContextName_____ cedarValueDomainContextName = new org.metadatacenter.ingestor.cedar.ContextName_____();
    cedarValueDomainContextName.setValue(cadsrValueDomainContextName);
    cedarValueDomain.setContextName(cedarValueDomainContextName);

    //value domain context version
    String cadsrValueDomainContextVersion = cadsrVALUEDOMAIN.getContextVersion().getContent();
    //System.out.println(cadsrValueDomainContextVersion);
    org.metadatacenter.ingestor.cedar.ContextVersion_____ cedarValueDomainContextVersion = new org.metadatacenter.ingestor.cedar.ContextVersion_____();
    cedarValueDomainContextVersion.setValue(cadsrValueDomainContextVersion);
    cedarValueDomain.setContextVersion(cedarValueDomainContextVersion);


    convertCADSR2CEDARValueDomainConceptualDomain(cedarValueDomain, cadsrVALUEDOMAIN);


    //value domain attributes continued
    //System.out.println("**Value Domain cont**");

    //value domain datatype
    String cadsrValueDomainDatatype = cadsrVALUEDOMAIN.getDatatype().getContent();
    //System.out.println(cadsrValueDomainDatatype);
    org.metadatacenter.ingestor.cedar.Datatype cedarValueDomainDatatype = new org.metadatacenter.ingestor.cedar.Datatype();
    cedarValueDomainDatatype.setValue(cadsrValueDomainDatatype);
    cedarValueDomain.setDatatype(cedarValueDomainDatatype);

    String cadsrValueDomainType = cadsrVALUEDOMAIN.getValueDomainType().getContent();
    //System.out.println(cadsrValueDomainType);
    org.metadatacenter.ingestor.cedar.ValueDomainType cedarValueDomainType = new org.metadatacenter.ingestor.cedar.ValueDomainType();
    cedarValueDomainType.setValue(cadsrValueDomainType);
    cedarValueDomain.setValueDomainType(cedarValueDomainType);

    //unit of measure
    String cadsrValueDomainUnitsOfMeasure = cadsrVALUEDOMAIN.getUnitOfMeasure().getContent();
    org.metadatacenter.ingestor.cedar.UnitsOfMeasure cedarValueDomainUnitsOfMeasure = new org.metadatacenter.ingestor.cedar.UnitsOfMeasure();
    if (cadsrValueDomainUnitsOfMeasure.equals("")) {
      cadsrValueDomainUnitsOfMeasure = cadsrVALUEDOMAIN.getUnitOfMeasure().getNULL();
      if (cadsrValueDomainUnitsOfMeasure.equals("TRUE")) {
        cadsrValueDomainUnitsOfMeasure = "NULL";
      }
    }
    //System.out.println(cadsrValueDomainUnitsOfMeasure);
    cedarValueDomainUnitsOfMeasure.setValue(cadsrValueDomainUnitsOfMeasure);
    cedarValueDomain.setUnitsOfMeasure(cedarValueDomainUnitsOfMeasure);

    //display format
    String cadsrValueDomainDisplayFormat = cadsrVALUEDOMAIN.getDisplayFormat().getContent();
    org.metadatacenter.ingestor.cedar.DisplayFormat cedarValueDomainDisplayFormat = new org.metadatacenter.ingestor.cedar.DisplayFormat();
    if (cadsrValueDomainDisplayFormat.equals("")) {
      cadsrValueDomainDisplayFormat = cadsrVALUEDOMAIN.getDisplayFormat().getNULL();
      if (cadsrValueDomainDisplayFormat.equals("TRUE")) {
        cadsrValueDomainDisplayFormat = "NULL";
      }
    }
    //System.out.println(cadsrValueDomainDisplayFormat);
    cedarValueDomainDisplayFormat.setValue(cadsrValueDomainDisplayFormat);
    cedarValueDomain.setDisplayFormat(cedarValueDomainDisplayFormat);

    //value domain maximum length
    String cadsrValueDomainMaximumLength = cadsrVALUEDOMAIN.getMaximumLength().getContent();
    if (cadsrValueDomainMaximumLength.equals("") && cadsrVALUEDOMAIN.getMaximumLength().getNULL().equals("TRUE")) {
      cadsrValueDomainMaximumLength = "NULL";
    }
    //System.out.println(cadsrValueDomainMaximumLength);
    org.metadatacenter.ingestor.cedar.MaximumLength cedarValueDomainMaximumLength = new org.metadatacenter.ingestor.cedar.MaximumLength();
    cedarValueDomainMaximumLength.setValue(cadsrValueDomainMaximumLength);
    cedarValueDomain.setMaximumLength(cedarValueDomainMaximumLength);

    //value domain minimum length
    String cadsrValueDomainMinimumLength = cadsrVALUEDOMAIN.getMinimumLength().getContent();
    if (cadsrValueDomainMinimumLength.equals("")) {
      cadsrValueDomainMinimumLength = cadsrVALUEDOMAIN.getMinimumLength().getNULL();
      if (cadsrValueDomainMinimumLength.equals("TRUE")) {
        cadsrValueDomainMinimumLength = "NULL";
      }
    }
    //System.out.println(cadsrValueDomainMinimumLength);
    org.metadatacenter.ingestor.cedar.MinimumLength cedarValueDomainMinimumLength = new org.metadatacenter.ingestor.cedar.MinimumLength();
    cedarValueDomainMinimumLength.setValue(cadsrValueDomainMinimumLength);
    cedarValueDomain.setMinimumLength(cedarValueDomainMinimumLength);

    //decimal place
    String cadsrValueDomainDecimalPlace = cadsrVALUEDOMAIN.getDecimalPlace().getContent();
    org.metadatacenter.ingestor.cedar.DecimalPlace cedarValueDomainDecimalPlace = new org.metadatacenter.ingestor.cedar.DecimalPlace();
    if (cadsrValueDomainDecimalPlace.equals("")) {
      cadsrValueDomainDecimalPlace = cadsrVALUEDOMAIN.getDecimalPlace().getNULL();
      if (cadsrValueDomainDecimalPlace.equals("TRUE")) {
        cadsrValueDomainDecimalPlace = "NULL";
      }
    }
    //System.out.println(cadsrValueDomainDecimalPlace);
    cedarValueDomainDecimalPlace.setValue(cadsrValueDomainDecimalPlace);
    cedarValueDomain.setDecimalPlace(cedarValueDomainDecimalPlace);

    //character set name
    String cadsrValueDomainCharacterSetName = cadsrVALUEDOMAIN.getCharacterSetName().getContent();
    org.metadatacenter.ingestor.cedar.CharacterSetName cedarValueDomainCharacterSetName = new org.metadatacenter.ingestor.cedar.CharacterSetName();
    if (cadsrValueDomainCharacterSetName.equals("")) {
      cadsrValueDomainCharacterSetName = cadsrVALUEDOMAIN.getCharacterSetName().getNULL();
      if (cadsrValueDomainCharacterSetName.equals("TRUE")) {
        cadsrValueDomainCharacterSetName = "NULL";
      }
    }
    //System.out.println(cadsrValueDomainCharacterSetName);
    cedarValueDomainCharacterSetName.setValue(cadsrValueDomainCharacterSetName);
    cedarValueDomain.setCharacterSetName(cedarValueDomainCharacterSetName);

    //max value
    String cadsrValueDomainMaximumValue = cadsrVALUEDOMAIN.getMaximumValue().getContent();
    org.metadatacenter.ingestor.cedar.MaximumValue cedarValueDomainMaximumValue = new org.metadatacenter.ingestor.cedar.MaximumValue();
    if (cadsrValueDomainMaximumValue.equals("")) {
      cadsrValueDomainMaximumValue = cadsrVALUEDOMAIN.getMaximumValue().getNULL();
      if (cadsrValueDomainMaximumValue.equals("TRUE")) {
        cadsrValueDomainMaximumValue = "NULL";
      }
    }
    //System.out.println(cadsrValueDomainMaximumValue);
    cedarValueDomainMaximumValue.setValue(cadsrValueDomainMaximumValue);
    cedarValueDomain.setMaximumValue(cedarValueDomainMaximumValue);

    //min value
    String cadsrValueDomainMinimumValue = cadsrVALUEDOMAIN.getMinimumValue().getContent();
    org.metadatacenter.ingestor.cedar.MinimumValue cedarValueDomainMinimumValue = new org.metadatacenter.ingestor.cedar.MinimumValue();
    if (cadsrValueDomainMinimumValue.equals("")) {
      cadsrValueDomainMinimumValue = cadsrVALUEDOMAIN.getMinimumValue().getNULL();
      if (cadsrValueDomainMinimumValue.equals("TRUE")) {
        cadsrValueDomainMinimumValue = "NULL";
      }
    }
    //System.out.println(cadsrValueDomainMinimumValue);
    cedarValueDomainMinimumValue.setValue(cadsrValueDomainMinimumValue);
    cedarValueDomain.setMinimumValue(cedarValueDomainMinimumValue);

    //origin
    String cadsrValueDomainOrigin = cadsrVALUEDOMAIN.getOrigin().getContent();
    org.metadatacenter.ingestor.cedar.Origin____ cedarValueDomainOrigin = new org.metadatacenter.ingestor.cedar.Origin____();
    if (cadsrValueDomainOrigin.equals("")) {
      cadsrValueDomainOrigin = cadsrVALUEDOMAIN.getOrigin().getNULL();
      if (cadsrValueDomainOrigin.equals("TRUE")) {
        cadsrValueDomainOrigin = "NULL";
      }
    }
    //System.out.println(cadsrValueDomainOrigin);
    cedarValueDomainOrigin.setValue(cadsrValueDomainOrigin);
    cedarValueDomain.setOrigin(cedarValueDomainOrigin);



    convertCADSR2CEDARRepresentations(cedarValueDomain, cadsrVALUEDOMAIN);


    convertCADSR2CEDARPermissibleValues(cedarValueDomain, cadsrVALUEDOMAIN);


    convertCADSR2CEDARValueDomainConcepts(cedarValueDomain, cadsrVALUEDOMAIN);

    cedarDataElement.setValueDomain(cedarValueDomain);
  }

  private static void convertCADSR2CEDARValueDomainConceptualDomain(ValueDomain cedarValueDomain, VALUEDOMAIN cadsrVALUEDOMAIN) {
    //value domain conceptual domain
    //System.out.println("**VD Conceptual Domain**");
    ConceptualDomain cadsrVDConceptualDomain = cadsrVALUEDOMAIN.getConceptualDomain();
    org.metadatacenter.ingestor.cedar.ConceptualDomain_ cedarVDConceptualDomain = new org.metadatacenter.ingestor.cedar.ConceptualDomain_();
    // URI id = URI.create("https://example.com/Dummy");
    //cedarVDConceptualDomain.setId(id);

    //value domain conceptual domain public id
    String cadsrVDConceptualDomainPublicID = cadsrVDConceptualDomain.getPublicId().getContent();
    //System.out.println(cadsrVDConceptualDomainPublicID);
    org.metadatacenter.ingestor.cedar.PublicID______ cedarVDConceptualDomainPublicID = new org.metadatacenter.ingestor.cedar.PublicID______();
    cedarVDConceptualDomainPublicID.setValue(cadsrVDConceptualDomainPublicID);
    cedarVDConceptualDomain.setPublicID(cedarVDConceptualDomainPublicID);

    //value domain conceptual domain context name
    String cadsrVDConceptualDomainContextName = cadsrVDConceptualDomain.getContextName().getContent();
    //System.out.println(cadsrVDConceptualDomainContextName);
    org.metadatacenter.ingestor.cedar.ContextName______ cedarVDConceptualDomainContextName = new org.metadatacenter.ingestor.cedar.ContextName______();
    cedarVDConceptualDomainContextName.setValue(cadsrVDConceptualDomainContextName);
    cedarVDConceptualDomain.setContextName(cedarVDConceptualDomainContextName);

    //value domain conceptual domain context version
    String cadsrVDConceptualDomainContextVersion = cadsrVDConceptualDomain.getContextVersion().getContent();
    //System.out.println(cadsrVDConceptualDomainContextVersion);
    org.metadatacenter.ingestor.cedar.ContextVersion______ cedarVDConceptualDomainContextVersion = new org.metadatacenter.ingestor.cedar.ContextVersion______();
    cedarVDConceptualDomainContextVersion.setValue(cadsrVDConceptualDomainContextVersion);
    cedarVDConceptualDomain.setContextVersion(cedarVDConceptualDomainContextVersion);

    //value domain conceptual domain preferred name
    String cadsrVDConceptualDomainPreferredName = cadsrVDConceptualDomain.getPreferredName().getContent();
    //System.out.println(cadsrVDConceptualDomainPreferredName);
    org.metadatacenter.ingestor.cedar.PreferredName________ cedarVDConceptualDomainPreferredName = new org.metadatacenter.ingestor.cedar.PreferredName________();
    cedarVDConceptualDomainPreferredName.setValue(cadsrVDConceptualDomainPreferredName);
    cedarVDConceptualDomain.setPreferredName(cedarVDConceptualDomainPreferredName);

    //value domain conceptual domain version
    String cadsrVDConceptualDomainVersion = cadsrVDConceptualDomain.getVersion().getContent();
    //System.out.println(cadsrVDConceptualDomainVersion);
    org.metadatacenter.ingestor.cedar.Version______ cedarVDConceptualDomainVersion = new org.metadatacenter.ingestor.cedar.Version______();
    cedarVDConceptualDomainVersion.setValue(cadsrVDConceptualDomainVersion);
    cedarVDConceptualDomain.setVersion(cedarVDConceptualDomainVersion);

    //value domain conceptual domain long name
    String cadsrVDConceptualDomainLongName = cadsrVDConceptualDomain.getLongName().getContent();
    //System.out.println(cadsrVDConceptualDomainLongName);
    org.metadatacenter.ingestor.cedar.LongName________ cedarVDConceptualDomainLongName = new org.metadatacenter.ingestor.cedar.LongName________();
    cedarVDConceptualDomainLongName.setValue(cadsrVDConceptualDomainLongName);
    cedarVDConceptualDomain.setLongName(cedarVDConceptualDomainLongName);

    cedarValueDomain.setConceptualDomain(cedarVDConceptualDomain);
  }

  private static void convertCADSR2CEDARRepresentations(ValueDomain cedarValueDomain, VALUEDOMAIN cadsrVALUEDOMAIN) {
    //representation
    //System.out.println("**Representation**");
    Representation cadsrValueDomainRepresentation = cadsrVALUEDOMAIN.getRepresentation();
    org.metadatacenter.ingestor.cedar.Representation cedarValueDomainRepresentation = new org.metadatacenter.ingestor.cedar.Representation();
    // URI id = URI.create("https://example.com/Dummy");
    //cedarValueDomainRepresentation.setId(id);

    //representation public ID
    String cadsrRepresentationPublicID = cadsrValueDomainRepresentation.getPublicId().getContent();
    //System.out.println(cadsrRepresentationPublicID);
    PublicID_______ cedarRepresentationPublicID = new PublicID_______();
    cedarRepresentationPublicID.setValue(cadsrRepresentationPublicID);
    cedarValueDomainRepresentation.setPublicID(cedarRepresentationPublicID);

    //representation context name
    String cadsrRepresentationContextName = cadsrValueDomainRepresentation.getContextName().getContent();
    //System.out.println(cadsrRepresentationContextName);
    ContextName_______ cedarRepresentationContextName = new org.metadatacenter.ingestor.cedar.ContextName_______();
    cedarRepresentationContextName.setValue(cadsrRepresentationContextName);
    cedarValueDomainRepresentation.setContextName(cedarRepresentationContextName);
    //TODO find out why this is "untitled" rather than "context name" (reported to waffle as bug)

    //representation context version
    String cadsrRepresentationContextVersion = cadsrValueDomainRepresentation.getContextVersion().getContent();
    //System.out.println(cadsrRepresentationContextVersion);
    ContextVersion_______ cedarRepresentationContextVersion = new ContextVersion_______();
    cedarRepresentationContextVersion.setValue(cadsrRepresentationContextVersion);
    cedarValueDomainRepresentation.setContextVersion(cedarRepresentationContextVersion);

    //representation preferred name
    String cadsrRepresentationPreferredName = cadsrValueDomainRepresentation.getPreferredName().getContent();
    //System.out.println(cadsrRepresentationPreferredName);
    PreferredName_________ cedarRepresentationPreferredName = new PreferredName_________();
    cedarRepresentationPreferredName.setValue(cadsrRepresentationPreferredName);
    cedarValueDomainRepresentation.setPreferredName(cedarRepresentationPreferredName);

    //representation version
    String cadsrRepresentationVersion = cadsrValueDomainRepresentation.getVersion().getContent();
    //System.out.println(cadsrRepresentationVersion);
    Version_______ cedarRepresentationVersion = new Version_______();
    cedarRepresentationVersion.setValue(cadsrRepresentationVersion);
    cedarValueDomainRepresentation.setVersion(cedarRepresentationVersion);

    //representation long name
    String cadsrRepresentationLongName = cadsrValueDomainRepresentation.getLongName().getContent();
    //System.out.println(cadsrRepresentationLongName);
    LongName_________ cedarRepresentationLongName = new LongName_________();
    cedarRepresentationLongName.setValue(cadsrRepresentationLongName);
    cedarValueDomainRepresentation.setLongName(cedarRepresentationLongName);

    convertCADSR2CEDARRepresentationConceptDetails(cedarValueDomainRepresentation, cadsrValueDomainRepresentation);


    cedarValueDomain.setRepresentation(cedarValueDomainRepresentation);
  }

  private static void convertCADSR2CEDARRepresentationConceptDetails(org.metadatacenter.ingestor.cedar.Representation cedarValueDomainRepresentation, Representation cadsrValueDomainRepresentation){
    //representation concept details list
    List<ConceptDetailsITEM> cadsrRepresentationConceptDetailsITEM = cadsrValueDomainRepresentation.getConceptDetails().getConceptDetailsITEM();
    org.metadatacenter.ingestor.cedar.ConceptDetails__ cedarRepresentationConceptDetails = new ConceptDetails__();
    // URI id = URI.create("https://example.com/Dummy");
    //cedarRepresentationConceptDetails.setId(id);
    List<ConceptDetailsItem__> cedarRepresentationConceptDetailsList = new ArrayList<ConceptDetailsItem__>();
    if (!cadsrRepresentationConceptDetailsITEM.isEmpty()) {
      for (ConceptDetailsITEM val : cadsrRepresentationConceptDetailsITEM) {

        org.metadatacenter.ingestor.cedar.ConceptDetailsItem__ cedarRepresentationConceptDetailsItem = new ConceptDetailsItem__();
        //cedarRepresentationConceptDetailsItem.setId(id);
        //System.out.println("representation concept details list item: ");

        //representation concept details preferred name
        String cadsrRepresentationConceptDetailsItemPreferredName = val.getPREFERREDNAME().getContent();
        //System.out.println(cadsrRepresentationConceptDetailsItemPreferredName);
        PreferredName__________ cedarRepresentationConceptDetailsItemPreferredName = new PreferredName__________();
        cedarRepresentationConceptDetailsItemPreferredName.setValue(cadsrRepresentationConceptDetailsItemPreferredName);
        cedarRepresentationConceptDetailsItem.setPreferredName(cedarRepresentationConceptDetailsItemPreferredName);

        //representation concept details long name
        String cadsrRepresentationConceptDetailsItemLongName = val.getLONGNAME().getContent();
        //System.out.println(cadsrRepresentationConceptDetailsItemLongName);
        LongName__________ cedarRepresentationConceptDetailsItemLongName = new LongName__________();
        cedarRepresentationConceptDetailsItemLongName.setValue(cadsrRepresentationConceptDetailsItemLongName);
        cedarRepresentationConceptDetailsItem.setLongName(cedarRepresentationConceptDetailsItemLongName);

        //representation concept details concept id
        String cadsrRepresentationConceptDetailsItemConceptID = val.getCONID().getContent();
        //System.out.println(cadsrRepresentationConceptDetailsItemConceptID);
        ConceptID__ cedarRepresentationConceptDetailsItemConceptID = new ConceptID__();
        cedarRepresentationConceptDetailsItemConceptID.setValue(cadsrRepresentationConceptDetailsItemConceptID);
        cedarRepresentationConceptDetailsItem.setConceptID(cedarRepresentationConceptDetailsItemConceptID);

        //representation concept details definition source
        String cadsrRepresentationConceptDetailsItemDefinitionSource = val.getDEFINITIONSOURCE().getContent();
        //System.out.println(cadsrRepresentationConceptDetailsItemDefinitionSource);
        DefinitionSource__ cedarRepresentationConceptDetailsItemDefinitionSource = new DefinitionSource__();
        cedarRepresentationConceptDetailsItemDefinitionSource.setValue(cadsrRepresentationConceptDetailsItemDefinitionSource);
        cedarRepresentationConceptDetailsItem.setDefinitionSource(cedarRepresentationConceptDetailsItemDefinitionSource);

        //representation concept details origin
        String cadsrRepresentationConceptDetailsItemOrigin = val.getORIGIN().getContent();
        if (cadsrRepresentationConceptDetailsItemOrigin.equals("") && val.getORIGIN().getNULL().equals("TRUE")) {
          cadsrRepresentationConceptDetailsItemOrigin = "NULL";
        }
        //System.out.println(cadsrRepresentationConceptDetailsItemOrigin);
        Origin_____ cedarRepresentationConceptDetailsItemOrigin = new Origin_____();
        cedarRepresentationConceptDetailsItemOrigin.setValue(cadsrRepresentationConceptDetailsItemOrigin);
        cedarRepresentationConceptDetailsItem.setOrigin(cedarRepresentationConceptDetailsItemOrigin);

        //representation concept details evs source
        String cadsrRepresentationConceptDetailsItemEVS = val.getEVSSOURCE().getContent();
        //System.out.println(cadsrRepresentationConceptDetailsItemEVS);
        EVSSource__ cedarRepresentationConceptDetailsItemEVS = new EVSSource__();
        cedarRepresentationConceptDetailsItemEVS.setValue(cadsrRepresentationConceptDetailsItemEVS);
        cedarRepresentationConceptDetailsItem.setEVSSource(cedarRepresentationConceptDetailsItemEVS);

        //representation concept details primary flag indicator
        String cadsrRepresentationConceptDetailsItemPrimaryFlag = val.getPRIMARYFLAGIND().getContent();
        //System.out.println(cadsrRepresentationConceptDetailsItemPrimaryFlag);
        PrimaryFlag__ cedarRepresentationConceptDetailsItemPrimaryFlag = new PrimaryFlag__();
        cedarRepresentationConceptDetailsItemPrimaryFlag.setValue(cadsrRepresentationConceptDetailsItemPrimaryFlag);
        cedarRepresentationConceptDetailsItem.setPrimaryFlag(cedarRepresentationConceptDetailsItemPrimaryFlag);

        //representation concept details display order
        String cadsrRepresentationConceptDetailsItemDisplayOrder = val.getDISPLAYORDER().getContent();
        //System.out.println(cadsrRepresentationConceptDetailsItemDisplayOrder);
        DisplayOrder__ cedarRepresentationConceptDetailsItemDisplayOrder = new DisplayOrder__();
        cedarRepresentationConceptDetailsItemDisplayOrder.setValue(cadsrRepresentationConceptDetailsItemDisplayOrder);
        cedarRepresentationConceptDetailsItem.setDisplayOrder(cedarRepresentationConceptDetailsItemDisplayOrder);

        //add concept details item to list
        cedarRepresentationConceptDetailsList.add(cedarRepresentationConceptDetailsItem);
      }
    }

    cedarRepresentationConceptDetails.setConceptDetailsItem(cedarRepresentationConceptDetailsList);
    cedarValueDomainRepresentation.setConceptDetails(cedarRepresentationConceptDetails);
  }

  private static void convertCADSR2CEDARPermissibleValues(ValueDomain cedarValueDomain, VALUEDOMAIN cadsrVALUEDOMAIN) {
    //permissible values
    //System.out.println("**Permissible Values**");
    List<PermissibleValuesITEM> permissibleValuesITEMList = cadsrVALUEDOMAIN.getPermissibleValues().getPermissibleValuesITEM();
    PermissibleValues cedarPermissibleValues = new PermissibleValues();
    // URI id = URI.create("https://example.com/Dummy");
    //cedarPermissibleValues.setId(id);
    List<PermissibleValuesItem> cedarPermissibleValuesList = new ArrayList<PermissibleValuesItem>();
    if (!permissibleValuesITEMList.isEmpty()) {
      for (PermissibleValuesITEM val : permissibleValuesITEMList) {

        PermissibleValuesItem cedarPermissibleValuesItem = new PermissibleValuesItem();
        //cedarPermissibleValuesItem.setId(id);

        //System.out.println("permissible values list item: ");

        // permissible values item valid value
        String cadsrPermissibleValuesItemValidValue = val.getVALIDVALUE().getContent();
        //System.out.println(cadsrPermissibleValuesItemValidValue);
        ValidValue cedarPermissibleValuesItemValidValue = new ValidValue();
        cedarPermissibleValuesItemValidValue.setValue(cadsrPermissibleValuesItemValidValue);
        cedarPermissibleValuesItem.setValidValue(cedarPermissibleValuesItemValidValue);

        // permissible values item value meaning
        String cadsrPermissibleValuesItemValueMeaning = val.getVALUEMEANING().getContent();
        //System.out.println(cadsrPermissibleValuesItemValueMeaning);
        ValueMeaning cedarPermissibleValuesItemValueMeaning = new ValueMeaning();
        cedarPermissibleValuesItemValueMeaning.setValue(cadsrPermissibleValuesItemValueMeaning);
        cedarPermissibleValuesItem.setValueMeaning(cedarPermissibleValuesItemValueMeaning);

        // permissible values item meaning description
        String cadsrPermissibleValuesItemMeaningDescription = val.getMEANINGDESCRIPTION().getContent();
        //System.out.println(cadsrPermissibleValuesItemMeaningDescription);
        MeaningDescription cedarPermissibleValuesItemMeaningDescription = new MeaningDescription();
        cedarPermissibleValuesItemMeaningDescription.setValue(cadsrPermissibleValuesItemMeaningDescription);
        cedarPermissibleValuesItem.setMeaningDescription(cedarPermissibleValuesItemMeaningDescription);

        // permissible values item meaning concepts
        String cadsrPermissibleValuesItemMeaningConcepts = val.getMEANINGCONCEPTS().getContent();
        //System.out.println(cadsrPermissibleValuesItemMeaningConcepts);
        MeaningConcepts cedarPermissibleValuesItemMeaningConcepts = new MeaningConcepts();
        cedarPermissibleValuesItemMeaningConcepts.setValue(cadsrPermissibleValuesItemMeaningConcepts);
        cedarPermissibleValuesItem.setMeaningConcepts(cedarPermissibleValuesItemMeaningConcepts);

        // permissible values item pv begin date
        String cadsrPermissibleValuesItemPVBeginDate = val.getPVBEGINDATE().getContent();
        //System.out.println(cadsrPermissibleValuesItemPVBeginDate);
        if (cadsrPermissibleValuesItemPVBeginDate.equals("")) {
          cadsrPermissibleValuesItemPVBeginDate = val.getPVBEGINDATE().getNULL();
          if (cadsrPermissibleValuesItemPVBeginDate.equals("TRUE")) {
            cadsrPermissibleValuesItemPVBeginDate = "NULL";
          }
        }
        PVBeginDate cedarPermissibleValuesItemPVBeginDate = new PVBeginDate();
        cedarPermissibleValuesItemPVBeginDate.setValue(cadsrPermissibleValuesItemPVBeginDate);
        cedarPermissibleValuesItem.setPVBeginDate(cedarPermissibleValuesItemPVBeginDate);

        String cadsrPermissibleValuesItemPVEndDate = val.getPVENDDATE().getContent();
        PVEndDate cedarPermissibleValuesItemPVEndDate = new PVEndDate();
        if (cadsrPermissibleValuesItemPVEndDate.equals("")) {
          cadsrPermissibleValuesItemPVEndDate = val.getPVENDDATE().getNULL();
          if (cadsrPermissibleValuesItemPVEndDate.equals("TRUE")) {
            cadsrPermissibleValuesItemPVEndDate = "NULL";
          }
        }
        //System.out.println(cadsrPermissibleValuesItemPVEndDate);
        cedarPermissibleValuesItemPVEndDate.setValue(cadsrPermissibleValuesItemPVEndDate);
        cedarPermissibleValuesItem.setPVEndDate(cedarPermissibleValuesItemPVEndDate);

        //permissible values item vm public id
        String cadsrPermissibleValuesItemVMPublicID = val.getVMPUBLICID().getContent();
        //System.out.println(cadsrPermissibleValuesItemVMPublicID);
        VMPublicID cedarPermissibleValuesItemVMPublicID = new VMPublicID();
        cedarPermissibleValuesItemVMPublicID.setValue(cadsrPermissibleValuesItemVMPublicID);
        cedarPermissibleValuesItem.setVMPublicID(cedarPermissibleValuesItemVMPublicID);

        //permissible values item vm version
        String cadsrPermissibleValuesItemVMVersion = val.getVMVERSION().getContent();
        //System.out.println(cadsrPermissibleValuesItemVMVersion);
        VMVersion cedarPermissibleValuesItemVMVersion = new VMVersion();
        cedarPermissibleValuesItemVMVersion.setValue(cadsrPermissibleValuesItemVMVersion);
        cedarPermissibleValuesItem.setVMVersion(cedarPermissibleValuesItemVMVersion);

        cedarPermissibleValuesList.add(cedarPermissibleValuesItem);
      }
    }

    cedarPermissibleValues.setPermissibleValuesItem(cedarPermissibleValuesList);
    cedarValueDomain.setPermissibleValues(cedarPermissibleValues);
  }

  private static void convertCADSR2CEDARValueDomainConcepts(ValueDomain cedarValueDomain, VALUEDOMAIN cadsrVALUEDOMAIN) {
    // value domain concepts
    //System.out.println("**Value Domain Concepts**");
    List<ValueDomainConceptsITEM> valueDomainConceptsITEMList = cadsrVALUEDOMAIN.getValueDomainConcepts().getValueDomainConceptsITEM();
    org.metadatacenter.ingestor.cedar.ValueDomainConcepts cedarValueDomainConcepts = new ValueDomainConcepts();
    // URI id = URI.create("https://example.com/Dummy");
    //cedarValueDomainConcepts.setId(id);

    List<ValueDomainConceptsItem> cedarValueDomainConceptsList = new ArrayList<>();
    if (!valueDomainConceptsITEMList.isEmpty()) {
      for (ValueDomainConceptsITEM val : valueDomainConceptsITEMList) {

        ValueDomainConceptsItem cedarValueDomainConceptsItem = new ValueDomainConceptsItem();
        //cedarValueDomainConceptsItem.setId(id);

        //System.out.println("value domain concepts list item: ");

        //value domain concepts preferred name
        String cadsrValueDomainConceptsItemPreferredName = val.getPREFERREDNAME().getContent();
        //System.out.println(cadsrValueDomainConceptsItemPreferredName);
        PreferredName___________ cedarValueDomainConceptsItemPreferredName = new PreferredName___________();
        cedarValueDomainConceptsItemPreferredName.setValue(cadsrValueDomainConceptsItemPreferredName);
        cedarValueDomainConceptsItem.setPreferredName(cedarValueDomainConceptsItemPreferredName);

        //value domain concepts long name
        String cadsrValueDomainConceptsItemLongName = val.getLONGNAME().getContent();
        //System.out.println(cadsrValueDomainConceptsItemLongName);
        LongName___________ cedarValueDomainConceptsItemLongName = new LongName___________();
        cedarValueDomainConceptsItemLongName.setValue(cadsrValueDomainConceptsItemLongName);
        cedarValueDomainConceptsItem.setLongName(cedarValueDomainConceptsItemLongName);

        //value domain concepts concept id
        String cadsrValueDomainConceptsItemConceptID = val.getCONID().getContent();
        //System.out.println(cadsrValueDomainConceptsItemConceptID);
        ConceptID___ cedarValueDomainConceptsItemConceptID = new ConceptID___();
        cedarValueDomainConceptsItemConceptID.setValue(cadsrValueDomainConceptsItemConceptID);
        cedarValueDomainConceptsItem.setConceptID(cedarValueDomainConceptsItemConceptID);

        //value domain concepts definition source
        String cadsrValueDomainConceptsItemDefinitionSource = val.getDEFINITIONSOURCE().getContent();
        //System.out.println(cadsrValueDomainConceptsItemDefinitionSource);
        DefinitionSource___ cedarValueDomainConceptsItemDefinitionSource = new DefinitionSource___();
        cedarValueDomainConceptsItemDefinitionSource.setValue(cadsrValueDomainConceptsItemDefinitionSource);
        cedarValueDomainConceptsItem.setDefinitionSource(cedarValueDomainConceptsItemDefinitionSource);

        //value domain concepts origin
        String cadsrValueDomainConceptsItemOrigin = val.getORIGIN().getContent();
        if (cadsrValueDomainConceptsItemOrigin.equals("") && val.getORIGIN().getNULL().equals("TRUE")) {
          cadsrValueDomainConceptsItemOrigin = "NULL";
        }
        //System.out.println(cadsrValueDomainConceptsItemOrigin);
        Origin______ cedarValueDomainConceptsItemOrigin = new Origin______();
        cedarValueDomainConceptsItemOrigin.setValue(cadsrValueDomainConceptsItemOrigin);
        cedarValueDomainConceptsItem.setOrigin(cedarValueDomainConceptsItemOrigin);

        //value domain concepts evs source
        String cadsrValueDomainConceptsItemEVS = val.getEVSSOURCE().getContent();
        //System.out.println(cadsrValueDomainConceptsItemEVS);
        EVSSource___ cedarValueDomainConceptsItemEVS = new EVSSource___();
        cedarValueDomainConceptsItemEVS.setValue(cadsrValueDomainConceptsItemEVS);
        cedarValueDomainConceptsItem.setEVSSource(cedarValueDomainConceptsItemEVS);

        //value domain concepts primary flag indicator
        String cadsrValueDomainConceptsItemPrimaryFlag = val.getPRIMARYFLAGIND().getContent();
        //System.out.println(cadsrValueDomainConceptsItemPrimaryFlag);
        PrimaryFlag___ cedarValueDomainConceptsItemPrimaryFlag = new PrimaryFlag___();
        cedarValueDomainConceptsItemPrimaryFlag.setValue(cadsrValueDomainConceptsItemPrimaryFlag);
        cedarValueDomainConceptsItem.setPrimaryFlag(cedarValueDomainConceptsItemPrimaryFlag);

        //value domain concepts display order
        String cadsrValueDomainConceptsItemDisplayOrder = val.getDISPLAYORDER().getContent();
        //System.out.println(cadsrValueDomainConceptsItemDisplayOrder);
        DisplayOrder___ cedarValueDomainConceptsItemDisplayOrder = new DisplayOrder___();
        cedarValueDomainConceptsItemDisplayOrder.setValue(cadsrValueDomainConceptsItemDisplayOrder);
        cedarValueDomainConceptsItem.setDisplayOrder(cedarValueDomainConceptsItemDisplayOrder);


        cedarValueDomainConceptsList.add(cedarValueDomainConceptsItem);
      }
    }
    cedarValueDomainConcepts.setValueDomainConceptsItem(cedarValueDomainConceptsList);
    cedarValueDomain.setValueDomainConcepts(cedarValueDomainConcepts);
  }


  private static void convertCADSR2CEDARReferenceDocuments(org.metadatacenter.ingestor.cedar.DataElement cedarDataElement, DataElement cadsrDataElement) {
    //reference documents list
    //System.out.println("**Reference Documents**");
    List<REFERENCEDOCUMENTSLISTITEM> referencedocumentslistitemList = cadsrDataElement.getREFERENCEDOCUMENTSLIST().getREFERENCEDOCUMENTSLISTITEM();
    ReferenceDocumentsList cedarReferenceDocuments = new ReferenceDocumentsList();
    // URI id = URI.create("https://example.com/Dummy");
    //cedarReferenceDocuments.setId(id);
    List<ReferenceDocumentsListItem> cedarReferenceDocumentsList = new ArrayList<ReferenceDocumentsListItem>();
    if (!referencedocumentslistitemList.isEmpty()) {
      for (REFERENCEDOCUMENTSLISTITEM val : referencedocumentslistitemList) {

        ReferenceDocumentsListItem cedarReferenceDocumentsItem = new ReferenceDocumentsListItem();
        //cedarReferenceDocumentsItem.setId(id);

        //System.out.println("reference documents list item: ");


        //reference document item name
        String cadsrReferenceDocumentsItemName = val.getName().getContent();
        //System.out.println(cadsrReferenceDocumentsItemName);
        Name cedarReferenceDocumentsItemName = new Name();
        cedarReferenceDocumentsItemName.setValue(cadsrReferenceDocumentsItemName);
        cedarReferenceDocumentsItem.setName(cedarReferenceDocumentsItemName);

        //reference document item organization name
        String cadsrReferenceDocumentsItemOrganizationName = val.getOrganizationName().getContent();
        if (cadsrReferenceDocumentsItemOrganizationName.equals("")) {
          cadsrReferenceDocumentsItemOrganizationName = val.getOrganizationName().getNULL();
          if (cadsrReferenceDocumentsItemOrganizationName.equals("TRUE")) {
            cadsrReferenceDocumentsItemOrganizationName = "NULL";
          }
        }
        //System.out.println(cadsrReferenceDocumentsItemOrganizationName);
        OrganizationName cedarReferenceDocumentsItemOrganizationName = new OrganizationName();
        cedarReferenceDocumentsItemOrganizationName.setValue(cadsrReferenceDocumentsItemOrganizationName);
        cedarReferenceDocumentsItem.setOrganizationName(cedarReferenceDocumentsItemOrganizationName);

        //reference document item document type
        String cadsrReferenceDocumentsItemDocumentType = val.getDocumentType().getContent();
        //System.out.println(cadsrReferenceDocumentsItemDocumentType);
        DocumentType cedarReferenceDocumentsItemDocumentType = new DocumentType();
        cedarReferenceDocumentsItemDocumentType.setValue(cadsrReferenceDocumentsItemDocumentType);
        cedarReferenceDocumentsItem.setDocumentType(cedarReferenceDocumentsItemDocumentType);

        //reference document item document text
        String cadsrReferenceDocumentsItemDocumentText = val.getDocumentText().getContent();
        if (cadsrReferenceDocumentsItemDocumentText.equals("")) {
          cadsrReferenceDocumentsItemDocumentText = val.getDocumentText().getNULL();
          if (cadsrReferenceDocumentsItemDocumentText.equals("TRUE")) {
            cadsrReferenceDocumentsItemDocumentText = "NULL";
          }
        }
        //System.out.println(cadsrReferenceDocumentsItemDocumentText);
        DocumentText cedarReferenceDocumentsItemDocumentText = new DocumentText();
        cedarReferenceDocumentsItemDocumentText.setValue(cadsrReferenceDocumentsItemDocumentText);
        cedarReferenceDocumentsItem.setDocumentText(cedarReferenceDocumentsItemDocumentText);

        //reference document item url
        String cadsrReferenceDocumentsItemURL = val.getURL().getContent();
        if (cadsrReferenceDocumentsItemURL.equals("")) {
          cadsrReferenceDocumentsItemURL = val.getURL().getNULL();
          if (cadsrReferenceDocumentsItemURL.equals("TRUE")) {
            cadsrReferenceDocumentsItemURL = "NULL";
          }
        }
        //System.out.println(cadsrReferenceDocumentsItemURL);
        URL cedarReferenceDocumentsItemURL = new URL();
        cedarReferenceDocumentsItemURL.setValue(cadsrReferenceDocumentsItemURL);
        cedarReferenceDocumentsItem.setURL(cedarReferenceDocumentsItemURL);

        //reference document item language
        String cadsrReferenceDocumentsItemLanguage = val.getLanguage().getContent();
        //System.out.println(cadsrReferenceDocumentsItemLanguage);
        Language cedarReferenceDocumentsItemLanguage = new Language();
        cedarReferenceDocumentsItemLanguage.setValue(cadsrReferenceDocumentsItemLanguage);
        cedarReferenceDocumentsItem.setLanguage(cedarReferenceDocumentsItemLanguage);

        //reference document item display order
        String cadsrReferenceDocumentsItemDisplayOrder = val.getDisplayOrder().getContent();
        if (cadsrReferenceDocumentsItemDisplayOrder.equals("")) {
          cadsrReferenceDocumentsItemDisplayOrder = val.getDisplayOrder().getNULL();
          if (cadsrReferenceDocumentsItemDisplayOrder.equals("TRUE")) {
            cadsrReferenceDocumentsItemDisplayOrder = "NULL";
          }
        }
        //System.out.println(cadsrReferenceDocumentsItemDisplayOrder);
        DisplayOrder____ cedarReferenceDocumentsItemDisplayOrder = new DisplayOrder____();
        cedarReferenceDocumentsItemDisplayOrder.setValue(cadsrReferenceDocumentsItemDisplayOrder);
        cedarReferenceDocumentsItem.setDisplayOrder(cedarReferenceDocumentsItemDisplayOrder);

        cedarReferenceDocumentsList.add(cedarReferenceDocumentsItem);
      }
    }

    cedarReferenceDocuments.setReferenceDocumentsListItem(cedarReferenceDocumentsList);
    cedarDataElement.setReferenceDocumentsList(cedarReferenceDocuments);

  }

  private static void convertCADSR2CEDARClassifications(org.metadatacenter.ingestor.cedar.DataElement cedarDataElement, DataElement cadsrDataElement) {
    //classification items
    //System.out.println("**Classifications**");
    CLASSIFICATIONSLIST cadsrClassificationsList = cadsrDataElement.getCLASSIFICATIONSLIST();
    List<CLASSIFICATIONSLISTITEM> cadsrClassificationsListItem = cadsrClassificationsList.getCLASSIFICATIONSLISTITEM();
    org.metadatacenter.ingestor.cedar.ClassificationsList cedarClassifications = new ClassificationsList();
    // URI id = URI.create("https://example.com/Dummy");
    //cedarClassifications.setId(id);

    List<ClassificationsListItem> cedarClassificationsList = new ArrayList<ClassificationsListItem>();
    if (!cadsrClassificationsListItem.isEmpty()) {
      for (CLASSIFICATIONSLISTITEM val : cadsrClassificationsListItem) {

        ClassificationsListItem cedarClassificationsListItem = new ClassificationsListItem();
        //cedarClassificationsListItem.setId(id);

        //System.out.println("classifications list item: ");

        ClassificationScheme cadsrClassificationScheme = val.getClassificationScheme();
        org.metadatacenter.ingestor.cedar.ClassificationScheme cedarClassificationScheme = new org.metadatacenter.ingestor.cedar.ClassificationScheme();
        //cedarClassificationScheme.setId(id);
        //System.out.println(" \t classification scheme: ");


        convertCADSR2CEDARClassificationScheme(cedarClassificationScheme, cadsrClassificationScheme);
        cedarClassificationsListItem.setClassificationScheme(cedarClassificationScheme);


        //classification scheme attributes continued
        //System.out.println("classifications list item (cont): ");

        //classification scheme item name
        String cadsrCSIName = val.getClassificationSchemeItemName().getContent();
        //System.out.println(cadsrCSIName);
        ClassificationSchemeItemName cedarCSIName = new ClassificationSchemeItemName();
        cedarCSIName.setValue(cadsrCSIName);
        cedarClassificationsListItem.setClassificationSchemeItemName(cedarCSIName);

        //classification scheme item type
        String cadsrCSIType = val.getClassificationSchemeItemType().getContent();
        //System.out.println(cadsrCSIType);
        ClassificationSchemeItemType cedarCSIType = new ClassificationSchemeItemType();
        cedarCSIType.setValue(cadsrCSIType);
        cedarClassificationsListItem.setClassificationSchemeItemType(cedarCSIType);

        //classification scheme item public id
        String cadsrCSIPublicId = val.getCsiPublicId().getContent();
        //System.out.println(cadsrCSIPublicId);
        CSIPublicID cedarCSIPublicId = new CSIPublicID();
        cedarCSIPublicId.setValue(cadsrCSIPublicId);
        cedarClassificationsListItem.setCSIPublicID(cedarCSIPublicId);

        //classification scheme item version
        String cadsrCSIVersion = val.getCsiVersion().getContent();
        //System.out.println(cadsrCSIVersion);
        CSIVersion cedarCSIVersion = new CSIVersion();
        cedarCSIVersion.setValue(cadsrCSIVersion);
        cedarClassificationsListItem.setCSIVersion(cedarCSIVersion);

        cedarClassificationsList.add(cedarClassificationsListItem);
      }
    }

    cedarClassifications.setClassificationsListItem(cedarClassificationsList);
    cedarDataElement.setClassificationsList(cedarClassifications);
  }

  private static void convertCADSR2CEDARClassificationScheme(org.metadatacenter.ingestor.cedar.ClassificationScheme cedarClassificationScheme, ClassificationScheme cadsrClassificationScheme) {
    //classification scheme public id
    String cadsrClassificationSchemePublicID = cadsrClassificationScheme.getPublicId().getContent();
    //System.out.println(cadsrClassificationSchemePublicID);
    PublicID________ cedarClassificationSchemePublicID = new PublicID________();
    cedarClassificationSchemePublicID.setValue(cadsrClassificationSchemePublicID);
    cedarClassificationScheme.setPublicID(cedarClassificationSchemePublicID);


    //classification scheme context name
    String cadsrClassificationSchemeContextName = cadsrClassificationScheme.getContextName().getContent();
    //System.out.println(cadsrClassificationSchemeContextName);
    ContextName________ cedarClassificationSchemeContextName = new ContextName________();
    cedarClassificationSchemeContextName.setValue(cadsrClassificationSchemeContextName);
    cedarClassificationScheme.setContextName(cedarClassificationSchemeContextName);


    //classification scheme context version
    String cadsrClassificationSchemeContextVersion = cadsrClassificationScheme.getContextVersion().getContent();
    //System.out.println(cadsrClassificationSchemeContextVersion);
    ContextVersion________ cedarClassificationSchemeContextVersion = new ContextVersion________();
    cedarClassificationSchemeContextVersion.setValue(cadsrClassificationSchemeContextVersion);
    cedarClassificationScheme.setContextVersion(cedarClassificationSchemeContextVersion);


    //classification scheme preferred name
    String cadsrClassificationSchemePreferredName = cadsrClassificationScheme.getPreferredName().getContent();
    //System.out.println(cadsrClassificationSchemePreferredName);
    PreferredName____________ cedarClassificationSchemePreferredName = new PreferredName____________();
    cedarClassificationSchemePreferredName.setValue(cadsrClassificationSchemePreferredName);
    cedarClassificationScheme.setPreferredName(cedarClassificationSchemePreferredName);


    //classification scheme version
    String cadsrClassificationSchemeVersion = cadsrClassificationScheme.getVersion().getContent();
    //System.out.println(cadsrClassificationSchemeVersion);
    Version________ cedarClassificationSchemeVersion = new Version________();
    cedarClassificationSchemeVersion.setValue(cadsrClassificationSchemeVersion);
    cedarClassificationScheme.setVersion(cedarClassificationSchemeVersion);
  }

  private static void convertCADSR2CEDARAlternateNames(org.metadatacenter.ingestor.cedar.DataElement cedarDataElement, DataElement cadsrDataElement) {
    //alternate names
    //System.out.println("**Alternate Names**");
    ALTERNATENAMELIST cadsrALTERNATENAMELIST = cadsrDataElement.getALTERNATENAMELIST();
    List<ALTERNATENAMELISTITEM> cadsrALTERNATENAMELISTITEM = cadsrALTERNATENAMELIST.getALTERNATENAMELISTITEM();
    AlternateNameList cedarAlternateNames = new AlternateNameList();
    // URI id = URI.create("https://example.com/Dummy");
    //cedarAlternateNames.setId(id);
    List<AlternateNameListItem> cedarAlternateNameList = new ArrayList<AlternateNameListItem>();
    if (!cadsrALTERNATENAMELISTITEM.isEmpty()) {
      for (ALTERNATENAMELISTITEM val : cadsrALTERNATENAMELISTITEM) {

        AlternateNameListItem cedarAlternateNameListItem = new AlternateNameListItem();
        //cedarAlternateNameListItem.setId(id);

        //System.out.println("alternate list item: ");

        //alternate name list item context name
        String cadsrAlternateNameListItemContextName = val.getContextName().getContent();
        //System.out.println(cadsrAlternateNameListItemContextName);
        ContextName_________ cedarAlternateNameListItemContextName = new ContextName_________();
        cedarAlternateNameListItemContextName.setValue(cadsrAlternateNameListItemContextName);
        cedarAlternateNameListItem.setContextName(cedarAlternateNameListItemContextName);

        //alternate name list item
        String cadsrAlternateNameListItemContextVersion = val.getContextVersion().getContent();
        //System.out.println(cadsrAlternateNameListItemContextVersion);
        ContextVersion_________ cedarAlternateNameListItemContextVersion = new ContextVersion_________();
        cedarAlternateNameListItemContextVersion.setValue(cadsrAlternateNameListItemContextVersion);
        cedarAlternateNameListItem.setContextVersion(cedarAlternateNameListItemContextVersion);

        //alternate name list item
        String cadsrAlternateNameListItemAlternateName = val.getAlternateName().getContent();
        //System.out.println(cadsrAlternateNameListItemAlternateName);
        AlternateName cedarAlternateNameListItemAlternateName = new AlternateName();
        cedarAlternateNameListItemAlternateName.setValue(cadsrAlternateNameListItemAlternateName);
        cedarAlternateNameListItem.setAlternateName(cedarAlternateNameListItemAlternateName);

        //alternate name list item
        String cadsrAlternateNameListItemAlternateNameType = val.getAlternateNameType().getContent();
        //System.out.println(cadsrAlternateNameListItemAlternateNameType);
        AlternateNameType cedarAlternateNameListItemAlternateNameType = new AlternateNameType();
        cedarAlternateNameListItemAlternateNameType.setValue(cadsrAlternateNameListItemAlternateNameType);
        cedarAlternateNameListItem.setAlternateNameType(cedarAlternateNameListItemAlternateNameType);

        //alternate name list item
        String cadsrAlternateNameListItemLanguage = val.getLanguage().getContent();
        //System.out.println(cadsrAlternateNameListItemLanguage);
        Language_ cedarAlternateNameListItemLanguage = new Language_();
        cedarAlternateNameListItemLanguage.setValue(cadsrAlternateNameListItemLanguage);
        cedarAlternateNameListItem.setLanguage(cedarAlternateNameListItemLanguage);

        cedarAlternateNameList.add(cedarAlternateNameListItem);
      }
    }

    cedarAlternateNames.setAlternateNameListItem(cedarAlternateNameList);
    cedarDataElement.setAlternateNameList(cedarAlternateNames);
  }

  private static void convertCADSR2CEDARDataElementDerivation(org.metadatacenter.ingestor.cedar.DataElement cedarDataElement, DataElement cadsrDataElement) {
    //data element derivation
    //System.out.println("**Data Element Derivation**");
    DATAELEMENTDERIVATION cadsrDATAELEMENTDERIVATION = cadsrDataElement.getDATAELEMENTDERIVATION();
    DataElementDerivation cedarDataElementDerivation = new DataElementDerivation();
    // URI id = URI.create("https://example.com/Dummy");
    //cedarDataElementDerivation.setId(id);

    //derivation type
    String cadsrDataElementDerivationType = cadsrDATAELEMENTDERIVATION.getDerivationType().getContent();
    if (cadsrDataElementDerivationType.equals("")) {
      cadsrDataElementDerivationType = cadsrDATAELEMENTDERIVATION.getDerivationType().getNULL();
      if (cadsrDataElementDerivationType.equals("TRUE")) {
        cadsrDataElementDerivationType = "NULL";
      }
    }
    //System.out.println(cadsrDataElementDerivationType);
    DerivationType cedarDataElementDerivationType = new DerivationType();
    cedarDataElementDerivationType.setValue(cadsrDataElementDerivationType);
    cedarDataElementDerivation.setDerivationType(cedarDataElementDerivationType);


    //derivation type description
    String cadsrDataElementDerivationTypeDescription = cadsrDATAELEMENTDERIVATION.getDerivationTypeDescription().getContent();
    if (cadsrDataElementDerivationTypeDescription.equals("")) {
      cadsrDataElementDerivationTypeDescription = cadsrDATAELEMENTDERIVATION.getDerivationTypeDescription().getNULL();
      if (cadsrDataElementDerivationTypeDescription.equals("TRUE")) {
        cadsrDataElementDerivationTypeDescription = "NULL";
      }
    }
    //System.out.println(cadsrDataElementDerivationTypeDescription);
    DerivationTypeDescription cedarDataElementDerivationTypeDescription = new DerivationTypeDescription();
    cedarDataElementDerivationTypeDescription.setValue(cadsrDataElementDerivationTypeDescription);
    cedarDataElementDerivation.setDerivationTypeDescription(cedarDataElementDerivationTypeDescription);


    //methods
    String cadsrDataElementDerivationMethods = cadsrDATAELEMENTDERIVATION.getMethods().getContent();
    if (cadsrDataElementDerivationMethods.equals("")) {
      cadsrDataElementDerivationMethods = cadsrDATAELEMENTDERIVATION.getMethods().getNULL();
      if (cadsrDataElementDerivationMethods.equals("TRUE")) {
        cadsrDataElementDerivationMethods = "NULL";
      }
    }
    //System.out.println(cadsrDataElementDerivationMethods);
    Methods cedarDataElementDerivationMethods = new Methods();
    cedarDataElementDerivationMethods.setValue(cadsrDataElementDerivationMethods);
    cedarDataElementDerivation.setMethods(cedarDataElementDerivationMethods);

    //rule
    String cadsrDataElementDerivationRule = cadsrDATAELEMENTDERIVATION.getRule().getContent();
    if (cadsrDataElementDerivationRule.equals("")) {
      cadsrDataElementDerivationRule = cadsrDATAELEMENTDERIVATION.getRule().getNULL();
      if (cadsrDataElementDerivationRule.equals("TRUE")) {
        cadsrDataElementDerivationRule = "NULL";
      }
    }
    //System.out.println(cadsrDataElementDerivationRule);
    Rule cedarDataElementDerivationRule = new Rule();
    cedarDataElementDerivationRule.setValue(cadsrDataElementDerivationRule);
    cedarDataElementDerivation.setRule(cedarDataElementDerivationRule);

    //concatenation character
    String cadsrDataElementDerivationConcatenationCharacter = cadsrDATAELEMENTDERIVATION.getConcatenationCharacter().getContent();
    if (cadsrDataElementDerivationConcatenationCharacter.equals("")) {
      cadsrDataElementDerivationConcatenationCharacter = cadsrDATAELEMENTDERIVATION.getConcatenationCharacter().getNULL();
      if (cadsrDataElementDerivationConcatenationCharacter.equals("TRUE")) {
        cadsrDataElementDerivationConcatenationCharacter = "NULL";
      }
    }
    //System.out.println(cadsrDataElementDerivationConcatenationCharacter);
    ConcatenationCharacter cedarDataElementDerivationConcatenationCharacter = new ConcatenationCharacter();
    cedarDataElementDerivationConcatenationCharacter.setValue(cadsrDataElementDerivationConcatenationCharacter);
    cedarDataElementDerivation.setConcatenationCharacter(cedarDataElementDerivationConcatenationCharacter);



    convertCADSR2CEDARComponentDataElements(cedarDataElementDerivation, cadsrDATAELEMENTDERIVATION);

    cedarDataElement.setDataElementDerivation(cedarDataElementDerivation);
  }

  private static void convertCADSR2CEDARComponentDataElements(DataElementDerivation cedarDataElementDerivation, DATAELEMENTDERIVATION cadsrDATAELEMENTDERIVATION) {
    //component data elements
    //System.out.println("**Component Data Elements**");
    List<ComponentDataElementsListITEM> cadsrComponenentDataElementsList = cadsrDATAELEMENTDERIVATION.getComponentDataElementsList().getComponentDataElementsListITEM();
    ComponentDataElementsList cedarComponentDataElements = new ComponentDataElementsList();
    // URI id = URI.create("https://example.com/Dummy");
    //cedarComponentDataElements.setId(id);

    List<ComponentDataElementsListItem> cedarComponentDataElementsList = new ArrayList<ComponentDataElementsListItem>();
    String componentDataElementsListNULLval = null;
    if (cadsrComponenentDataElementsList.isEmpty()) {
      componentDataElementsListNULLval = cadsrDATAELEMENTDERIVATION.getComponentDataElementsList().getNULL();
      if (componentDataElementsListNULLval==null) {
        componentDataElementsListNULLval = "NULL";
      } else if (componentDataElementsListNULLval.equals("TRUE")) {
        componentDataElementsListNULLval = "NULL";
      }
      //System.out.println(componenentDataElementsListNULLval);
      //cedarComponentDataElements.setComponentDataElementsListItem(componentDataElementsListNULLval); //TODO figure out
    } else {
      for (ComponentDataElementsListITEM val : cadsrComponenentDataElementsList) {

        ComponentDataElementsListItem cedarComponentDataElementsListItem = new ComponentDataElementsListItem();
        //cedarComponentDataElementsListItem.setId(id);

        //System.out.println("component data elements list item: ");


        //component data elements public id
        String cadsrComponentDataElementsListItemPublicID = val.getPublicId().getContent();
        //System.out.println(cadsrComponentDataElementsListItemPublicID);
        PublicID_________ cedarComponentDataElementsListItemPublicID = new PublicID_________();
        cedarComponentDataElementsListItemPublicID.setValue(cadsrComponentDataElementsListItemPublicID);
        cedarComponentDataElementsListItem.setPublicID(cedarComponentDataElementsListItemPublicID);

        //component data elements long name
        String cadsrComponentDataElementsListItemLongName = val.getLongName().getContent();
        //System.out.println(cadsrComponentDataElementsListItemLongName);
        LongName____________ cedarComponentDataElementsListItemLongName = new LongName____________();
        cedarComponentDataElementsListItemLongName.setValue(cadsrComponentDataElementsListItemLongName);
        cedarComponentDataElementsListItem.setLongName(cedarComponentDataElementsListItemLongName);

        //component data elements preferred name
        String cadsrComponentDataElementsListItemPreferredName = val.getPreferredName().getContent();
        //System.out.println(cadsrComponentDataElementsListItemPreferredName);
        PreferredName_____________ cedarComponentDataElementsListItemPreferredName = new PreferredName_____________();
        cedarComponentDataElementsListItemPreferredName.setValue(cadsrComponentDataElementsListItemPreferredName);
        cedarComponentDataElementsListItem.setPreferredName(cedarComponentDataElementsListItemPreferredName);

        //component data elements preferred definition
        String cadsrComponentDataElementsListItemPreferredDefinition = val.getPreferredDefinition().getContent();
        //System.out.println(cadsrComponentDataElementsListItemPreferredDefinition);
        PreferredDefinition___ cedarComponentDataElementsListItemPreferredDefinition = new PreferredDefinition___();
        cedarComponentDataElementsListItemPreferredDefinition.setValue(cadsrComponentDataElementsListItemPreferredDefinition);
        cedarComponentDataElementsListItem.setPreferredDefinition(cedarComponentDataElementsListItemPreferredDefinition);

        //component data elements version
        String cadsrComponentDataElementsListItemVersion = val.getVersion().getContent();
        //System.out.println(cadsrComponentDataElementsListItemVersion);
        Version_________ cedarComponentDataElementsListItemVersion = new Version_________();
        cedarComponentDataElementsListItemVersion.setValue(cadsrComponentDataElementsListItemVersion);
        cedarComponentDataElementsListItem.setVersion(cedarComponentDataElementsListItemVersion);

        //component data elements workflow status
        String cadsrComponentDataElementsListItemWorkflowStatus = val.getWorkflowStatus().getContent();
        //System.out.println(cadsrComponentDataElementsListItemWorkflowStatus);
        WorkflowStatus___ cedarComponentDataElementsListItemWorkflowStatus = new WorkflowStatus___();
        cedarComponentDataElementsListItemWorkflowStatus.setValue(cadsrComponentDataElementsListItemWorkflowStatus);
        cedarComponentDataElementsListItem.setWorkflowStatus(cedarComponentDataElementsListItemWorkflowStatus);

        //component data elements context name
        String cadsrComponentDataElementsListItemContextName = val.getContextName().getContent();
        //System.out.println(cadsrComponentDataElementsListItemContextName);
        ContextName__________ cedarComponentDataElementsListItemContextName = new ContextName__________();
        cedarComponentDataElementsListItemContextName.setValue(cadsrComponentDataElementsListItemContextName);
        cedarComponentDataElementsListItem.setContextName(cedarComponentDataElementsListItemContextName);

        //component data elements display order
        String cadsrComponentDataElementsListItemDisplayOrder = val.getDisplayOrder().getContent();
        //System.out.println(cadsrComponentDataElementsListItemDisplayOrder);
        DisplayOrder_____ cedarComponentDataElementsListItemDisplayOrder = new DisplayOrder_____();
        cedarComponentDataElementsListItemDisplayOrder.setValue(cadsrComponentDataElementsListItemDisplayOrder);
        cedarComponentDataElementsListItem.setDisplayOrder(cedarComponentDataElementsListItemDisplayOrder);


        cedarComponentDataElementsList.add(cedarComponentDataElementsListItem);
      }
    }

    cedarComponentDataElements.setComponentDataElementsListItem(cedarComponentDataElementsList);
    cedarDataElementDerivation.setComponentDataElementsList(cedarComponentDataElements);
  }


}

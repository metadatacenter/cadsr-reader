package org.metadatacenter.cadsr;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class CDEXMLInstancesReader
{
  public static void main(String[] argc) throws IOException, JAXBException, DatatypeConfigurationException
  {

    // Create JAXB XML unmarshaller
    JAXBContext jaxbContext = JAXBContext.newInstance(DataElementsList.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

    File xmlFile;
    File[] xmlFileList; //how to use file array
    String xmlDirectoryName;
    DataElementsList dataElementsList;
    if (argc.length == 0) {
      xmlDirectoryName = "src/main/resources/xml/example/";
      xmlFile = new File(xmlDirectoryName);
      xmlFileList = xmlFile.listFiles();
    } else {
      xmlDirectoryName = argc[0];
      File directory = new File(xmlDirectoryName);
      xmlFileList = directory.listFiles();
    }
    if (xmlFileList == null) {
      System.out.println("There are no files in this directory.");
      System.exit(1);
    }
    //check for null, exit if no files to process
    for (File file : xmlFileList) {
      String filename = file.getName();

      if (file.isFile() && (filename.endsWith(".xml") || filename.endsWith(".XML"))) {
        // Read a list of DataElement objects from an XML file containing a list of CDEs
        dataElementsList = ((DataElementsList)jaxbUnmarshaller.unmarshal(file));

        for (DataElement cadsrDataElement : dataElementsList.dataElement) {
          processDataElement(cadsrDataElement);
        }
      } else {
        System.out.println(String.format("Invalid file: %s", filename));
      }
    }
    for (File print_file : xmlFileList) {
      System.out.println(print_file.toString());
    }
  }

  private static void processDataElement(DataElement cadsrDataElement)
  {
    // Process each DataElement
    //System.out.println("Processing DataElement....");

    processCADSR2CEDARDataElementAttributes(cadsrDataElement);
    processCADSR2CEDARDataElementConcept(cadsrDataElement);
    processCADSR2CEDARValueDomain(cadsrDataElement);
    processCADSR2CEDARReferenceDocuments(cadsrDataElement);
    processCADSR2CEDARClassifications(cadsrDataElement);
    processCADSR2CEDARAlternateNames(cadsrDataElement);
    processCADSR2CEDARDataElementDerivation(cadsrDataElement);
  }

  private static void processCADSR2CEDARDataElementAttributes(DataElement cadsrDataElement)
  {

    //data element details
    //System.out.println("**Data Element Details**");

    //DE public ID
    String cadsrPublicID = cadsrDataElement.getPUBLICID().getContent();
    //System.out.println(cadsrPublicID);

    //DE Long Name
    String cadsrLongName = cadsrDataElement.getLONGNAME().getContent();
    //System.out.println(cadsrLongName);

    //DE Preferred Name
    String cadsrPreferredName = cadsrDataElement.getPREFERREDNAME().getContent();
    //System.out.println(cadsrPreferredName);

    //DE Preferred Definition
    String cadsrPreferredDefinition = cadsrDataElement.getPREFERREDDEFINITION().getContent();
    //System.out.println(cadsrPreferredDefinition);

    //DE Version
    String cadsrVersion = cadsrDataElement.getVERSION().getContent();
    //System.out.println(cadsrVersion);

    //DE Workflow Status
    String cadsrWorkflowStatus = cadsrDataElement.getWORKFLOWSTATUS().getContent();
    //System.out.println(cadsrWorkflowStatus);

    //DE Context Name
    String cadsrContextName = cadsrDataElement.getCONTEXTNAME().getContent();
    //System.out.println(cadsrContextName);

    //DE Context Version
    String cadsrContextVersion = cadsrDataElement.getCONTEXTVERSION().getContent();
    //System.out.println(cadsrContextVersion);

    //DE Origin
    String cadsrOrigin = cadsrDataElement.getORIGIN().getContent();
    if (cadsrOrigin.equals("")) {
      cadsrOrigin = cadsrDataElement.getORIGIN().getNULL();
      if (cadsrOrigin.equals("TRUE")) {
        cadsrOrigin = "NULL";
      }
    }
    //System.out.println(cadsrOrigin);

    //DE Registration Status
    String cadsrRegistrationStatus = cadsrDataElement.getREGISTRATIONSTATUS().getContent();
    //System.out.println(cadsrRegistrationStatus);
  }

  private static void processCADSR2CEDARDataElementConcept(DataElement cadsrDataElement)
  {
    // build Data element concept from xml to json
    //System.out.println("**Data Element Concept**");
    DATAELEMENTCONCEPT cadsrDATAELEMENTCONCEPT = cadsrDataElement.getDATAELEMENTCONCEPT();
    // URI id = URI.create("https://example.com/Dummy");
    //cedarDataElementConcept.setId(id);

    //DEC Public ID
    String cadsrDECPublicID = cadsrDATAELEMENTCONCEPT.getPublicId().getContent();
    //System.out.println(cadsrDECPublicID);

    //DEC Preferred Name
    String cadsrDECPreferredName = cadsrDATAELEMENTCONCEPT.getPreferredName().getContent();
    //System.out.println(cadsrDECPreferredName);

    //DEC Preferred Definition
    String cadsrDECPreferredDefinition = cadsrDATAELEMENTCONCEPT.getPreferredDefinition().getContent();
    //System.out.println(cadsrDECPreferredDefinition);

    //DEC Long Name
    String cadsrDECLongName = cadsrDATAELEMENTCONCEPT.getLongName().getContent();
    //System.out.println(cadsrDECLongName);

    //DEC Version
    String cadsrDECVersion = cadsrDATAELEMENTCONCEPT.getVersion().getContent();
    //System.out.println(cadsrDECVersion);

    //DEC Workflow Status
    String cadsrDECWorkflowStatus = cadsrDATAELEMENTCONCEPT.getWorkflowStatus().getContent();
    //System.out.println(cadsrDECWorkflowStatus);

    //DEC Context Name
    String cadsrDECContextName = cadsrDATAELEMENTCONCEPT.getContextName().getContent();
    //System.out.println(cadsrDECContextName);

    //DEC Context Version
    String cadsrDECContextVersion = cadsrDATAELEMENTCONCEPT.getContextVersion().getContent();
    //System.out.println(cadsrDECContextVersion);

    processCADSR2CEDARDataElementConceptConceptualDomain(cadsrDATAELEMENTCONCEPT);
    processCADSR2CEDARObjectClass(cadsrDATAELEMENTCONCEPT);
    processCADSR2CEDARProperty(cadsrDATAELEMENTCONCEPT);

    processCADSR2CEDARObjectClassQualifier(cadsrDATAELEMENTCONCEPT);

    processCADSR2CEDARPropertyQualifier(cadsrDATAELEMENTCONCEPT);

    processCADSR2CEDAROrigin(cadsrDATAELEMENTCONCEPT);
  }

  private static void processCADSR2CEDARDataElementConceptConceptualDomain(DATAELEMENTCONCEPT cadsrDATAELEMENTCONCEPT)
  {
    //DEC conceptual domain
    //System.out.println("**DEC Conceptual Domain**");
    ConceptualDomain cadsrDATAELEMENTCONCEPTDECConceptualDomain = cadsrDATAELEMENTCONCEPT.getConceptualDomain();

    //DEC CD public id
    String cadsrDECcdPublicID = cadsrDATAELEMENTCONCEPTDECConceptualDomain.getPublicId().getContent();
    //System.out.println(cadsrDECcdPublicID);

    //DEC CD context name
    String cadsrDECcdContextName = cadsrDATAELEMENTCONCEPTDECConceptualDomain.getContextName().getContent();
    //System.out.println(cadsrDECcdContextName);

    //DEC CD context version
    String cadsrDECcdContextVersion = cadsrDATAELEMENTCONCEPTDECConceptualDomain.getContextVersion().getContent();
    //System.out.println(cadsrDECcdContextVersion);

    //DEC CD preferred name
    String cadsrDECcdPreferredName = cadsrDATAELEMENTCONCEPTDECConceptualDomain.getPreferredName().getContent();
    //System.out.println(cadsrDECcdPreferredName);

    //DEC CD version
    String cadsrDECcdVersion = cadsrDATAELEMENTCONCEPTDECConceptualDomain.getVersion().getContent();
    //System.out.println(cadsrDECcdVersion);

    //DEC CD long name
    String cadsrDECcdLongName = cadsrDATAELEMENTCONCEPTDECConceptualDomain.getLongName().getContent();
    //System.out.println(cadsrDECcdLongName);
  }

  private static void processCADSR2CEDARObjectClass(DATAELEMENTCONCEPT cadsrDATAELEMENTCONCEPT)
  {
    //object class
    //System.out.println("**ObjectClass**");
    ObjectClass cadsrDATAELEMENTCONCEPTObjectClass = cadsrDATAELEMENTCONCEPT.getObjectClass();

    //object class public id
    String cadsrObjClassPublicID = cadsrDATAELEMENTCONCEPTObjectClass.getPublicId().getContent();
    //System.out.println(cadsrObjClassPublicID);

    //object class context name
    String cadsrObjClassContextName = cadsrDATAELEMENTCONCEPTObjectClass.getContextName().getContent();
    //System.out.println(cadsrObjClassContextName);

    //object class context version
    String cadsrObjClassContextVersion = cadsrDATAELEMENTCONCEPTObjectClass.getContextVersion().getContent();
    //System.out.println(cadsrObjClassContextVersion);

    //object class preferred name
    String cadsrObjClassPreferredName = cadsrDATAELEMENTCONCEPTObjectClass.getPreferredName().getContent();
    //System.out.println(cadsrObjClassPreferredName);

    //object class version
    String cadsrObjClassVersion = cadsrDATAELEMENTCONCEPTObjectClass.getVersion().getContent();
    //System.out.println(cadsrObjClassVersion);

    //object class long name
    String cadsrObjClassLongName = cadsrDATAELEMENTCONCEPTObjectClass.getLongName().getContent();
    //System.out.println(cadsrObjClassLongName);
    processCADSR2CEDARObjectClassConceptDetails(cadsrDATAELEMENTCONCEPTObjectClass);
  }

  private static void processCADSR2CEDARObjectClassConceptDetails(ObjectClass cadsrDATAELEMENTCONCEPTObjectClass)
  {
    //object class concept details list
    List<ConceptDetailsITEM> cadsrDATAELEMENTCONCEPTObjectClassConceptDetailsITEM = cadsrDATAELEMENTCONCEPTObjectClass
      .getConceptDetails().getConceptDetailsITEM();

    if (!cadsrDATAELEMENTCONCEPTObjectClassConceptDetailsITEM.isEmpty()) {
      for (ConceptDetailsITEM val : cadsrDATAELEMENTCONCEPTObjectClassConceptDetailsITEM) {

        //obj class concept details preferred name
        String cadsrObjClassConceptDetailsItemPreferredName = val.getPREFERREDNAME().getContent();
        //System.out.println(cadsrObjClassConceptDetailsItemPreferredName);

        //obj class concept details long name
        String cadsrObjClassConceptDetailsItemLongName = val.getLONGNAME().getContent();
        //System.out.println(cadsrObjClassConceptDetailsItemLongName);

        //obj class concept details concept id
        String cadsrObjClassConceptDetailsItemConID = val.getCONID().getContent();
        //System.out.println(cadsrObjClassConceptDetailsItemConID);

        //obj class concept details definition source
        String cadsrObjClassConceptDetailsItemDefSource = val.getDEFINITIONSOURCE().getContent();
        //System.out.println(cadsrObjClassConceptDetailsItemDefSource);

        //obj class concept details origin
        String cadsrObjClassConceptDetailsItemOrigin = val.getORIGIN().getContent();
        //System.out.println(cadsrObjClassConceptDetailsItemOrigin);

        //obj class concept details EVS Source
        String cadsrObjClassConceptDetailsItemEVS = val.getEVSSOURCE().getContent();
        //System.out.println(cadsrObjClassConceptDetailsItemEVS);

        //obj class concept details primary flag indicator
        String cadsrObjClassConceptDetailsItemPrimaryFlag = val.getPRIMARYFLAGIND().getContent();
        //System.out.println(cadsrObjClassConceptDetailsItemPrimaryFlag);

        //obj class concept details display order
        String cadsrObjClassConceptDetailsItemDisplayOrder = val.getDISPLAYORDER().getContent();
        //System.out.println(cadsrObjClassConceptDetailsItemDisplayOrder);
      }
    }
  }

  private static void processCADSR2CEDARProperty(DATAELEMENTCONCEPT cadsrDATAELEMENTCONCEPT)
  {
    //property
    //System.out.println("**Property**");
    Property cadsrDATAELEMENTCONCEPTProperty = cadsrDATAELEMENTCONCEPT.getProperty();

    //property public id
    String cadsrPropertyPublicID = cadsrDATAELEMENTCONCEPTProperty.getPublicId().getContent();
    //System.out.println(cadsrPropertyPublicID);

    //property context name
    String cadsrPropertyContextName = cadsrDATAELEMENTCONCEPTProperty.getContextName().getContent();
    //System.out.println(cadsrPropertyContextName);

    //property context version
    String cadsrPropertyContextVersion = cadsrDATAELEMENTCONCEPTProperty.getContextVersion().getContent();
    //System.out.println(cadsrPropertyContextVersion);

    //property preferred name
    String cadsrPropertyPreferredName = cadsrDATAELEMENTCONCEPTProperty.getPreferredName().getContent();
    //System.out.println(cadsrPropertyPreferredName);

    //property version
    String cadsrPropertyVersion = cadsrDATAELEMENTCONCEPTProperty.getVersion().getContent();
    //System.out.println(cadsrPropertyVersion);

    //property long name
    String cadsrPropertyLongName = cadsrDATAELEMENTCONCEPTProperty.getLongName().getContent();
    //System.out.println(cadsrPropertyLongName);

    processCADSR2CEDARPropertyConceptDetails(cadsrDATAELEMENTCONCEPTProperty);
  }

  private static void processCADSR2CEDARPropertyConceptDetails(Property cadsrDATAELEMENTCONCEPTProperty)
  {
    //property concept details list
    List<ConceptDetailsITEM> cadsrDATAELEMENTCONCEPTPropertyConceptDetailsITEM = cadsrDATAELEMENTCONCEPTProperty
      .getConceptDetails().getConceptDetailsITEM();

    if (!cadsrDATAELEMENTCONCEPTPropertyConceptDetailsITEM.isEmpty()) {
      for (ConceptDetailsITEM val : cadsrDATAELEMENTCONCEPTPropertyConceptDetailsITEM) {
        //property concept details preferred name
        String cadsrPropertyConceptDetailsItemPreferredName = val.getPREFERREDNAME().getContent();
        //System.out.println(cadsrPropertyConceptDetailsItemPreferredName);

        //property concept details long name
        String cadsrPropertyConceptDetailsItemLongName = val.getLONGNAME().getContent();
        //System.out.println(cadsrPropertyConceptDetailsItemLongName);

        //property concept details concept id
        String cadsrPropertyConceptDetailsItemConID = val.getCONID().getContent();
        //System.out.println(cadsrPropertyConceptDetailsItemConID);

        //property concept details definition source
        String cadsrPropertyConceptDetailsItemDefSource = val.getDEFINITIONSOURCE().getContent();
        //System.out.println(cadsrPropertyConceptDetailsItemDefSource);

        //property concept details origin
        String cadsrPropertyConceptDetailsItemOrigin = val.getORIGIN().getContent();
        //System.out.println(cadsrPropertyConceptDetailsItemOrigin);

        //property concept details EVS Source
        String cadsrPropertyConceptDetailsItemEVS = val.getEVSSOURCE().getContent();
        //System.out.println(cadsrPropertyConceptDetailsItemEVS);

        //property concept details primary flag indicator
        String cadsrPropertyConceptDetailsItemPrimaryFlag = val.getPRIMARYFLAGIND().getContent();
        //System.out.println(cadsrPropertyConceptDetailsItemPrimaryFlag);

        //property concept details display order
        String cadsrPropertyConceptDetailsItemDisplayOrder = val.getDISPLAYORDER().getContent();
        //System.out.println(cadsrPropertyConceptDetailsItemDisplayOrder);
      }
    }
  }

  private static void processCADSR2CEDARObjectClassQualifier(DATAELEMENTCONCEPT cadsrDATAELEMENTCONCEPT)
  {
    //object class qualifier
    String cadsrObjectClassQualifier = cadsrDATAELEMENTCONCEPT.getObjectClassQualifier().getContent();

    if (cadsrObjectClassQualifier.equals("")) {
      cadsrObjectClassQualifier = cadsrDATAELEMENTCONCEPT.getObjectClassQualifier().getNULL();
      if (cadsrObjectClassQualifier.equals("TRUE")) {
        cadsrObjectClassQualifier = "NULL";
      }
    }
    //System.out.println(cadsrObjectClassQualifier);
  }

  private static void processCADSR2CEDARPropertyQualifier(DATAELEMENTCONCEPT cadsrDATAELEMENTCONCEPT)
  {
    //property qualifier
    String cadsrPropertyQualifier = cadsrDATAELEMENTCONCEPT.getPropertyQualifier().getContent();

    if (cadsrPropertyQualifier.equals("")) { //TODO does this work instead of ".isEmpty()"?
      cadsrPropertyQualifier = cadsrDATAELEMENTCONCEPT.getPropertyQualifier().getNULL();
      if (cadsrPropertyQualifier.equals("TRUE")) {
        cadsrPropertyQualifier = "NULL";
      }
    }
    //System.out.println(cadsrPropertyQualifier);
  }

  private static void processCADSR2CEDAROrigin(DATAELEMENTCONCEPT cadsrDATAELEMENTCONCEPT)
  {
    //origin
    String cadsrDECOrigin = cadsrDATAELEMENTCONCEPT.getOrigin().getContent();

    if (cadsrDECOrigin.equals("")) {
      cadsrDECOrigin = cadsrDATAELEMENTCONCEPT.getOrigin().getNULL();
      if (cadsrDECOrigin.equals("TRUE")) {
        cadsrDECOrigin = "NULL";
      }
    }
    //System.out.println(cadsrDECOrigin);
  }

  private static void processCADSR2CEDARValueDomain(DataElement cadsrDataElement)
  {
    //value domain
    //System.out.println("**Value Domain**");
    VALUEDOMAIN cadsrVALUEDOMAIN = cadsrDataElement.getVALUEDOMAIN();

    //value domain public id
    String cadsrValueDomainPublicID = cadsrVALUEDOMAIN.getPublicId().getContent();
    //System.out.println(cadsrValueDomainPublicID);

    //value domain preferred name
    String cadsrValueDomainPreferredName = cadsrVALUEDOMAIN.getPreferredName().getContent();
    //System.out.println(cadsrValueDomainPreferredName);

    //value domain preferred definition
    String cadsrValueDomainPreferredDefinition = cadsrVALUEDOMAIN.getPreferredDefinition().getContent();
    //System.out.println(cadsrValueDomainPreferredDefinition);

    //value domain long name
    String cadsrValueDomainLongName = cadsrVALUEDOMAIN.getLongName().getContent();
    //System.out.println(cadsrValueDomainLongName);

    //value domain version
    String cadsrValueDomainVersion = cadsrVALUEDOMAIN.getVersion().getContent();
    //System.out.println(cadsrValueDomainVersion);

    //value domain workflow status
    String cadsrValueDomainWorkflowStatus = cadsrVALUEDOMAIN.getWorkflowStatus().getContent();
    //System.out.println(cadsrValueDomainWorkflowStatus);

    //value domain context name
    String cadsrValueDomainContextName = cadsrVALUEDOMAIN.getContextName().getContent();
    //System.out.println(cadsrValueDomainContextName);

    //value domain context version
    String cadsrValueDomainContextVersion = cadsrVALUEDOMAIN.getContextVersion().getContent();
    //System.out.println(cadsrValueDomainContextVersion);

    processCADSR2CEDARValueDomainConceptualDomain(cadsrVALUEDOMAIN);

    //value domain attributes continued
    //System.out.println("**Value Domain cont**");

    //value domain datatype
    String cadsrValueDomainDatatype = cadsrVALUEDOMAIN.getDatatype().getContent();
    //System.out.println(cadsrValueDomainDatatype);

    String cadsrValueDomainType = cadsrVALUEDOMAIN.getValueDomainType().getContent();
    //System.out.println(cadsrValueDomainType);

    //unit of measure
    String cadsrValueDomainUnitsOfMeasure = cadsrVALUEDOMAIN.getUnitOfMeasure().getContent();
    if (cadsrValueDomainUnitsOfMeasure.equals("")) {
      cadsrValueDomainUnitsOfMeasure = cadsrVALUEDOMAIN.getUnitOfMeasure().getNULL();
      if (cadsrValueDomainUnitsOfMeasure.equals("TRUE")) {
        cadsrValueDomainUnitsOfMeasure = "NULL";
      }
    }
    //System.out.println(cadsrValueDomainUnitsOfMeasure);

    //display format
    String cadsrValueDomainDisplayFormat = cadsrVALUEDOMAIN.getDisplayFormat().getContent();
    if (cadsrValueDomainDisplayFormat.equals("")) {
      cadsrValueDomainDisplayFormat = cadsrVALUEDOMAIN.getDisplayFormat().getNULL();
      if (cadsrValueDomainDisplayFormat.equals("TRUE")) {
        cadsrValueDomainDisplayFormat = "NULL";
      }
    }
    //System.out.println(cadsrValueDomainDisplayFormat);

    //value domain maximum length
    String cadsrValueDomainMaximumLength = cadsrVALUEDOMAIN.getMaximumLength().getContent();
    if (cadsrValueDomainMaximumLength.equals("") && cadsrVALUEDOMAIN.getMaximumLength().getNULL().equals("TRUE")) {
      cadsrValueDomainMaximumLength = "NULL";
    }
    //System.out.println(cadsrValueDomainMaximumLength);

    //value domain minimum length
    String cadsrValueDomainMinimumLength = cadsrVALUEDOMAIN.getMinimumLength().getContent();
    if (cadsrValueDomainMinimumLength.equals("")) {
      cadsrValueDomainMinimumLength = cadsrVALUEDOMAIN.getMinimumLength().getNULL();
      if (cadsrValueDomainMinimumLength.equals("TRUE")) {
        cadsrValueDomainMinimumLength = "NULL";
      }
    }
    //System.out.println(cadsrValueDomainMinimumLength);

    //decimal place
    String cadsrValueDomainDecimalPlace = cadsrVALUEDOMAIN.getDecimalPlace().getContent();
    if (cadsrValueDomainDecimalPlace.equals("")) {
      cadsrValueDomainDecimalPlace = cadsrVALUEDOMAIN.getDecimalPlace().getNULL();
      if (cadsrValueDomainDecimalPlace.equals("TRUE")) {
        cadsrValueDomainDecimalPlace = "NULL";
      }
    }
    //System.out.println(cadsrValueDomainDecimalPlace);

    //character set name
    String cadsrValueDomainCharacterSetName = cadsrVALUEDOMAIN.getCharacterSetName().getContent();
    if (cadsrValueDomainCharacterSetName.equals("")) {
      cadsrValueDomainCharacterSetName = cadsrVALUEDOMAIN.getCharacterSetName().getNULL();
      if (cadsrValueDomainCharacterSetName.equals("TRUE")) {
        cadsrValueDomainCharacterSetName = "NULL";
      }
    }
    //System.out.println(cadsrValueDomainCharacterSetName);

    //max value
    String cadsrValueDomainMaximumValue = cadsrVALUEDOMAIN.getMaximumValue().getContent();
    if (cadsrValueDomainMaximumValue.equals("")) {
      cadsrValueDomainMaximumValue = cadsrVALUEDOMAIN.getMaximumValue().getNULL();
      if (cadsrValueDomainMaximumValue.equals("TRUE")) {
        cadsrValueDomainMaximumValue = "NULL";
      }
    }
    //System.out.println(cadsrValueDomainMaximumValue);

    //min value
    String cadsrValueDomainMinimumValue = cadsrVALUEDOMAIN.getMinimumValue().getContent();
    if (cadsrValueDomainMinimumValue.equals("")) {
      cadsrValueDomainMinimumValue = cadsrVALUEDOMAIN.getMinimumValue().getNULL();
      if (cadsrValueDomainMinimumValue.equals("TRUE")) {
        cadsrValueDomainMinimumValue = "NULL";
      }
    }
    //System.out.println(cadsrValueDomainMinimumValue);

    //origin
    String cadsrValueDomainOrigin = cadsrVALUEDOMAIN.getOrigin().getContent();
    if (cadsrValueDomainOrigin.equals("")) {
      cadsrValueDomainOrigin = cadsrVALUEDOMAIN.getOrigin().getNULL();
      if (cadsrValueDomainOrigin.equals("TRUE")) {
        cadsrValueDomainOrigin = "NULL";
      }
    }
    processCADSR2CEDARRepresentations(cadsrVALUEDOMAIN);

    processCADSR2CEDARPermissibleValues(cadsrVALUEDOMAIN);

    processCADSR2CEDARValueDomainConcepts(cadsrVALUEDOMAIN);
  }

  private static void processCADSR2CEDARValueDomainConceptualDomain(VALUEDOMAIN cadsrVALUEDOMAIN)
  {
    //value domain conceptual domain
    //System.out.println("**VD Conceptual Domain**");
    ConceptualDomain cadsrVDConceptualDomain = cadsrVALUEDOMAIN.getConceptualDomain();

    //value domain conceptual domain public id
    String cadsrVDConceptualDomainPublicID = cadsrVDConceptualDomain.getPublicId().getContent();
    //System.out.println(cadsrVDConceptualDomainPublicID);

    //value domain conceptual domain context name
    String cadsrVDConceptualDomainContextName = cadsrVDConceptualDomain.getContextName().getContent();
    //System.out.println(cadsrVDConceptualDomainContextName);

    //value domain conceptual domain context version
    String cadsrVDConceptualDomainContextVersion = cadsrVDConceptualDomain.getContextVersion().getContent();
    //System.out.println(cadsrVDConceptualDomainContextVersion);

    //value domain conceptual domain preferred name
    String cadsrVDConceptualDomainPreferredName = cadsrVDConceptualDomain.getPreferredName().getContent();
    //System.out.println(cadsrVDConceptualDomainPreferredName);

    //value domain conceptual domain version
    String cadsrVDConceptualDomainVersion = cadsrVDConceptualDomain.getVersion().getContent();
    //System.out.println(cadsrVDConceptualDomainVersion);

    //value domain conceptual domain long name
    String cadsrVDConceptualDomainLongName = cadsrVDConceptualDomain.getLongName().getContent();
    //System.out.println(cadsrVDConceptualDomainLongName);
  }

  private static void processCADSR2CEDARRepresentations(VALUEDOMAIN cadsrVALUEDOMAIN)
  {
    //representation
    //System.out.println("**Representation**");
    Representation cadsrValueDomainRepresentation = cadsrVALUEDOMAIN.getRepresentation();

    //representation public ID
    String cadsrRepresentationPublicID = cadsrValueDomainRepresentation.getPublicId().getContent();
    //System.out.println(cadsrRepresentationPublicID);

    //representation context name
    String cadsrRepresentationContextName = cadsrValueDomainRepresentation.getContextName().getContent();
    //System.out.println(cadsrRepresentationContextName);

    //representation context version
    String cadsrRepresentationContextVersion = cadsrValueDomainRepresentation.getContextVersion().getContent();
    //System.out.println(cadsrRepresentationContextVersion);

    //representation preferred name
    String cadsrRepresentationPreferredName = cadsrValueDomainRepresentation.getPreferredName().getContent();
    //System.out.println(cadsrRepresentationPreferredName);

    //representation version
    String cadsrRepresentationVersion = cadsrValueDomainRepresentation.getVersion().getContent();
    //System.out.println(cadsrRepresentationVersion);

    //representation long name
    String cadsrRepresentationLongName = cadsrValueDomainRepresentation.getLongName().getContent();
    //System.out.println(cadsrRepresentationLongName);

    processCADSR2CEDARRepresentationConceptDetails(cadsrValueDomainRepresentation);
  }

  private static void processCADSR2CEDARRepresentationConceptDetails(Representation cadsrValueDomainRepresentation)
  {
    //representation concept details list
    List<ConceptDetailsITEM> cadsrRepresentationConceptDetailsITEM = cadsrValueDomainRepresentation.getConceptDetails()
      .getConceptDetailsITEM();

    if (!cadsrRepresentationConceptDetailsITEM.isEmpty()) {
      for (ConceptDetailsITEM val : cadsrRepresentationConceptDetailsITEM) {

        //representation concept details preferred name
        String cadsrRepresentationConceptDetailsItemPreferredName = val.getPREFERREDNAME().getContent();
        //System.out.println(cadsrRepresentationConceptDetailsItemPreferredName);

        //representation concept details long name
        String cadsrRepresentationConceptDetailsItemLongName = val.getLONGNAME().getContent();
        //System.out.println(cadsrRepresentationConceptDetailsItemLongName);

        //representation concept details concept id
        String cadsrRepresentationConceptDetailsItemConceptID = val.getCONID().getContent();
        //System.out.println(cadsrRepresentationConceptDetailsItemConceptID);

        //representation concept details definition source
        String cadsrRepresentationConceptDetailsItemDefinitionSource = val.getDEFINITIONSOURCE().getContent();
        //System.out.println(cadsrRepresentationConceptDetailsItemDefinitionSource);

        //representation concept details origin
        String cadsrRepresentationConceptDetailsItemOrigin = val.getORIGIN().getContent();
        if (cadsrRepresentationConceptDetailsItemOrigin.equals("") && val.getORIGIN().getNULL().equals("TRUE")) {
          cadsrRepresentationConceptDetailsItemOrigin = "NULL";
        }
        //System.out.println(cadsrRepresentationConceptDetailsItemOrigin);

        //representation concept details evs source
        String cadsrRepresentationConceptDetailsItemEVS = val.getEVSSOURCE().getContent();
        //System.out.println(cadsrRepresentationConceptDetailsItemEVS);

        //representation concept details primary flag indicator
        String cadsrRepresentationConceptDetailsItemPrimaryFlag = val.getPRIMARYFLAGIND().getContent();
        //System.out.println(cadsrRepresentationConceptDetailsItemPrimaryFlag);

        //representation concept details display order
        String cadsrRepresentationConceptDetailsItemDisplayOrder = val.getDISPLAYORDER().getContent();
        //System.out.println(cadsrRepresentationConceptDetailsItemDisplayOrder);
      }
    }
  }

  private static void processCADSR2CEDARPermissibleValues(VALUEDOMAIN cadsrVALUEDOMAIN)
  {
    //permissible values
    //System.out.println("**Permissible Values**");
    List<PermissibleValuesITEM> permissibleValuesITEMList = cadsrVALUEDOMAIN.getPermissibleValues()
      .getPermissibleValuesITEM();
    PermissibleValues cedarPermissibleValues = new PermissibleValues();
    if (!permissibleValuesITEMList.isEmpty()) {
      for (PermissibleValuesITEM val : permissibleValuesITEMList) {

        // permissible values item valid value
        String cadsrPermissibleValuesItemValidValue = val.getVALIDVALUE().getContent();
        //System.out.println(cadsrPermissibleValuesItemValidValue);

        // permissible values item value meaning
        String cadsrPermissibleValuesItemValueMeaning = val.getVALUEMEANING().getContent();
        //System.out.println(cadsrPermissibleValuesItemValueMeaning);

        // permissible values item meaning description
        String cadsrPermissibleValuesItemMeaningDescription = val.getMEANINGDESCRIPTION().getContent();
        //System.out.println(cadsrPermissibleValuesItemMeaningDescription);

        // permissible values item meaning concepts
        String cadsrPermissibleValuesItemMeaningConcepts = val.getMEANINGCONCEPTS().getContent();
        //System.out.println(cadsrPermissibleValuesItemMeaningConcepts);

        // permissible values item pv begin date
        String cadsrPermissibleValuesItemPVBeginDate = val.getPVBEGINDATE().getContent();
        //System.out.println(cadsrPermissibleValuesItemPVBeginDate);
        if (cadsrPermissibleValuesItemPVBeginDate.equals("")) {
          cadsrPermissibleValuesItemPVBeginDate = val.getPVBEGINDATE().getNULL();
          if (cadsrPermissibleValuesItemPVBeginDate.equals("TRUE")) {
            cadsrPermissibleValuesItemPVBeginDate = "NULL";
          }
        }

        String cadsrPermissibleValuesItemPVEndDate = val.getPVENDDATE().getContent();
        if (cadsrPermissibleValuesItemPVEndDate.equals("")) {
          cadsrPermissibleValuesItemPVEndDate = val.getPVENDDATE().getNULL();
          if (cadsrPermissibleValuesItemPVEndDate.equals("TRUE")) {
            cadsrPermissibleValuesItemPVEndDate = "NULL";
          }
        }
        //System.out.println(cadsrPermissibleValuesItemPVEndDate);

        //permissible values item vm public id
        String cadsrPermissibleValuesItemVMPublicID = val.getVMPUBLICID().getContent();
        //System.out.println(cadsrPermissibleValuesItemVMPublicID);

        //permissible values item vm version
        String cadsrPermissibleValuesItemVMVersion = val.getVMVERSION().getContent();
        //System.out.println(cadsrPermissibleValuesItemVMVersion);
      }
    }
  }

  private static void processCADSR2CEDARValueDomainConcepts(VALUEDOMAIN cadsrVALUEDOMAIN)
  {
    // value domain concepts
    //System.out.println("**Value Domain Concepts**");
    List<ValueDomainConceptsITEM> valueDomainConceptsITEMList = cadsrVALUEDOMAIN.getValueDomainConcepts()
      .getValueDomainConceptsITEM();

    if (!valueDomainConceptsITEMList.isEmpty()) {
      for (ValueDomainConceptsITEM val : valueDomainConceptsITEMList) {

        //value domain concepts preferred name
        String cadsrValueDomainConceptsItemPreferredName = val.getPREFERREDNAME().getContent();
        //System.out.println(cadsrValueDomainConceptsItemPreferredName);

        //value domain concepts long name
        String cadsrValueDomainConceptsItemLongName = val.getLONGNAME().getContent();
        //System.out.println(cadsrValueDomainConceptsItemLongName);

        //value domain concepts concept id
        String cadsrValueDomainConceptsItemConceptID = val.getCONID().getContent();
        //System.out.println(cadsrValueDomainConceptsItemConceptID);

        //value domain concepts definition source
        String cadsrValueDomainConceptsItemDefinitionSource = val.getDEFINITIONSOURCE().getContent();
        //System.out.println(cadsrValueDomainConceptsItemDefinitionSource);

        //value domain concepts origin
        String cadsrValueDomainConceptsItemOrigin = val.getORIGIN().getContent();
        if (cadsrValueDomainConceptsItemOrigin.equals("") && val.getORIGIN().getNULL().equals("TRUE")) {
          cadsrValueDomainConceptsItemOrigin = "NULL";
        }
        //System.out.println(cadsrValueDomainConceptsItemOrigin);

        //value domain concepts evs source
        String cadsrValueDomainConceptsItemEVS = val.getEVSSOURCE().getContent();
        //System.out.println(cadsrValueDomainConceptsItemEVS);

        //value domain concepts primary flag indicator
        String cadsrValueDomainConceptsItemPrimaryFlag = val.getPRIMARYFLAGIND().getContent();
        //System.out.println(cadsrValueDomainConceptsItemPrimaryFlag);

        //value domain concepts display order
        String cadsrValueDomainConceptsItemDisplayOrder = val.getDISPLAYORDER().getContent();
        //System.out.println(cadsrValueDomainConceptsItemDisplayOrder);
      }
    }
  }

  private static void processCADSR2CEDARReferenceDocuments(DataElement cadsrDataElement)
  {
    //reference documents list
    //System.out.println("**Reference Documents**");
    List<REFERENCEDOCUMENTSLISTITEM> referencedocumentslistitemList = cadsrDataElement.getREFERENCEDOCUMENTSLIST()
      .getREFERENCEDOCUMENTSLISTITEM();
    if (!referencedocumentslistitemList.isEmpty()) {
      for (REFERENCEDOCUMENTSLISTITEM val : referencedocumentslistitemList) {

        //reference document item name
        String cadsrReferenceDocumentsItemName = val.getName().getContent();
        //System.out.println(cadsrReferenceDocumentsItemName);

        //reference document item organization name
        String cadsrReferenceDocumentsItemOrganizationName = val.getOrganizationName().getContent();
        if (cadsrReferenceDocumentsItemOrganizationName.equals("")) {
          cadsrReferenceDocumentsItemOrganizationName = val.getOrganizationName().getNULL();
          if (cadsrReferenceDocumentsItemOrganizationName.equals("TRUE")) {
            cadsrReferenceDocumentsItemOrganizationName = "NULL";
          }
        }
        //System.out.println(cadsrReferenceDocumentsItemOrganizationName);

        //reference document item document type
        String cadsrReferenceDocumentsItemDocumentType = val.getDocumentType().getContent();
        //System.out.println(cadsrReferenceDocumentsItemDocumentType);

        //reference document item document text
        String cadsrReferenceDocumentsItemDocumentText = val.getDocumentText().getContent();
        if (cadsrReferenceDocumentsItemDocumentText.equals("")) {
          cadsrReferenceDocumentsItemDocumentText = val.getDocumentText().getNULL();
          if (cadsrReferenceDocumentsItemDocumentText.equals("TRUE")) {
            cadsrReferenceDocumentsItemDocumentText = "NULL";
          }
        }
        //System.out.println(cadsrReferenceDocumentsItemDocumentText);

        //reference document item url
        String cadsrReferenceDocumentsItemURL = val.getURL().getContent();
        if (cadsrReferenceDocumentsItemURL.equals("")) {
          cadsrReferenceDocumentsItemURL = val.getURL().getNULL();
          if (cadsrReferenceDocumentsItemURL.equals("TRUE")) {
            cadsrReferenceDocumentsItemURL = "NULL";
          }
        }
        //System.out.println(cadsrReferenceDocumentsItemURL);

        //reference document item language
        String cadsrReferenceDocumentsItemLanguage = val.getLanguage().getContent();
        //System.out.println(cadsrReferenceDocumentsItemLanguage);
        Language cedarReferenceDocumentsItemLanguage = new Language();

        //reference document item display order
        String cadsrReferenceDocumentsItemDisplayOrder = val.getDisplayOrder().getContent();
        if (cadsrReferenceDocumentsItemDisplayOrder.equals("")) {
          cadsrReferenceDocumentsItemDisplayOrder = val.getDisplayOrder().getNULL();
          if (cadsrReferenceDocumentsItemDisplayOrder.equals("TRUE")) {
            cadsrReferenceDocumentsItemDisplayOrder = "NULL";
          }
        }
        //System.out.println(cadsrReferenceDocumentsItemDisplayOrder);
      }
    }
  }

  private static void processCADSR2CEDARClassifications(DataElement cadsrDataElement)
  {
    //classification items
    //System.out.println("**Classifications**");
    CLASSIFICATIONSLIST cadsrClassificationsList = cadsrDataElement.getCLASSIFICATIONSLIST();
    List<CLASSIFICATIONSLISTITEM> cadsrClassificationsListItem = cadsrClassificationsList.getCLASSIFICATIONSLISTITEM();

    if (!cadsrClassificationsListItem.isEmpty()) {
      for (CLASSIFICATIONSLISTITEM val : cadsrClassificationsListItem) {

        ClassificationScheme cadsrClassificationScheme = val.getClassificationScheme();

        processCADSR2CEDARClassificationScheme(cadsrClassificationScheme);

        //classification scheme attributes continued
        //System.out.println("classifications list item (cont): ");

        //classification scheme item name
        String cadsrCSIName = val.getClassificationSchemeItemName().getContent();
        //System.out.println(cadsrCSIName);

        //classification scheme item type
        String cadsrCSIType = val.getClassificationSchemeItemType().getContent();
        //System.out.println(cadsrCSIType);

        //classification scheme item public id
        String cadsrCSIPublicId = val.getCsiPublicId().getContent();
        //System.out.println(cadsrCSIPublicId);

        //classification scheme item version
        String cadsrCSIVersion = val.getCsiVersion().getContent();
        //System.out.println(cadsrCSIVersion);
      }
    }
  }

  private static void processCADSR2CEDARClassificationScheme(ClassificationScheme cadsrClassificationScheme)
  {
    //classification scheme public id
    String cadsrClassificationSchemePublicID = cadsrClassificationScheme.getPublicId().getContent();
    //System.out.println(cadsrClassificationSchemePublicID);

    //classification scheme context name
    String cadsrClassificationSchemeContextName = cadsrClassificationScheme.getContextName().getContent();
    //System.out.println(cadsrClassificationSchemeContextName);

    //classification scheme context version
    String cadsrClassificationSchemeContextVersion = cadsrClassificationScheme.getContextVersion().getContent();
    //System.out.println(cadsrClassificationSchemeContextVersion);

    //classification scheme preferred name
    String cadsrClassificationSchemePreferredName = cadsrClassificationScheme.getPreferredName().getContent();
    //System.out.println(cadsrClassificationSchemePreferredName);

    //classification scheme version
    String cadsrClassificationSchemeVersion = cadsrClassificationScheme.getVersion().getContent();
    //System.out.println(cadsrClassificationSchemeVersion);
  }

  private static void processCADSR2CEDARAlternateNames(DataElement cadsrDataElement)
  {
    //alternate names
    //System.out.println("**Alternate Names**");
    ALTERNATENAMELIST cadsrALTERNATENAMELIST = cadsrDataElement.getALTERNATENAMELIST();
    List<ALTERNATENAMELISTITEM> cadsrALTERNATENAMELISTITEM = cadsrALTERNATENAMELIST.getALTERNATENAMELISTITEM();
    if (!cadsrALTERNATENAMELISTITEM.isEmpty()) {
      for (ALTERNATENAMELISTITEM val : cadsrALTERNATENAMELISTITEM) {

        //alternate name list item context name
        String cadsrAlternateNameListItemContextName = val.getContextName().getContent();
        //System.out.println(cadsrAlternateNameListItemContextName);
        //alternate name list item

        String cadsrAlternateNameListItemContextVersion = val.getContextVersion().getContent();
        //System.out.println(cadsrAlternateNameListItemContextVersion);

        //alternate name list item
        String cadsrAlternateNameListItemAlternateName = val.getAlternateName().getContent();
        //System.out.println(cadsrAlternateNameListItemAlternateName);

        //alternate name list item
        String cadsrAlternateNameListItemAlternateNameType = val.getAlternateNameType().getContent();
        //System.out.println(cadsrAlternateNameListItemAlternateNameType);

        //alternate name list item
        String cadsrAlternateNameListItemLanguage = val.getLanguage().getContent();
        //System.out.println(cadsrAlternateNameListItemLanguage);
      }
    }
  }

  private static void processCADSR2CEDARDataElementDerivation(DataElement cadsrDataElement)
  {
    //data element derivation
    //System.out.println("**Data Element Derivation**");
    DATAELEMENTDERIVATION cadsrDATAELEMENTDERIVATION = cadsrDataElement.getDATAELEMENTDERIVATION();

    //derivation type
    String cadsrDataElementDerivationType = cadsrDATAELEMENTDERIVATION.getDerivationType().getContent();
    if (cadsrDataElementDerivationType.equals("")) {
      cadsrDataElementDerivationType = cadsrDATAELEMENTDERIVATION.getDerivationType().getNULL();
      if (cadsrDataElementDerivationType.equals("TRUE")) {
        cadsrDataElementDerivationType = "NULL";
      }
    }
    //System.out.println(cadsrDataElementDerivationType);

    //derivation type description
    String cadsrDataElementDerivationTypeDescription = cadsrDATAELEMENTDERIVATION.getDerivationTypeDescription()
      .getContent();
    if (cadsrDataElementDerivationTypeDescription.equals("")) {
      cadsrDataElementDerivationTypeDescription = cadsrDATAELEMENTDERIVATION.getDerivationTypeDescription().getNULL();
      if (cadsrDataElementDerivationTypeDescription.equals("TRUE")) {
        cadsrDataElementDerivationTypeDescription = "NULL";
      }
    }
    //System.out.println(cadsrDataElementDerivationTypeDescription);

    //methods
    String cadsrDataElementDerivationMethods = cadsrDATAELEMENTDERIVATION.getMethods().getContent();
    if (cadsrDataElementDerivationMethods.equals("")) {
      cadsrDataElementDerivationMethods = cadsrDATAELEMENTDERIVATION.getMethods().getNULL();
      if (cadsrDataElementDerivationMethods.equals("TRUE")) {
        cadsrDataElementDerivationMethods = "NULL";
      }
    }
    //System.out.println(cadsrDataElementDerivationMethods);

    //rule
    String cadsrDataElementDerivationRule = cadsrDATAELEMENTDERIVATION.getRule().getContent();
    if (cadsrDataElementDerivationRule.equals("")) {
      cadsrDataElementDerivationRule = cadsrDATAELEMENTDERIVATION.getRule().getNULL();
      if (cadsrDataElementDerivationRule.equals("TRUE")) {
        cadsrDataElementDerivationRule = "NULL";
      }
    }
    //System.out.println(cadsrDataElementDerivationRule);

    //concatenation character
    String cadsrDataElementDerivationConcatenationCharacter = cadsrDATAELEMENTDERIVATION.getConcatenationCharacter()
      .getContent();
    if (cadsrDataElementDerivationConcatenationCharacter.equals("")) {
      cadsrDataElementDerivationConcatenationCharacter = cadsrDATAELEMENTDERIVATION.getConcatenationCharacter()
        .getNULL();
      if (cadsrDataElementDerivationConcatenationCharacter.equals("TRUE")) {
        cadsrDataElementDerivationConcatenationCharacter = "NULL";
      }
    }
    //System.out.println(cadsrDataElementDerivationConcatenationCharacter);

    processCADSR2CEDARComponentDataElements(cadsrDATAELEMENTDERIVATION);
  }

  private static void processCADSR2CEDARComponentDataElements(DATAELEMENTDERIVATION cadsrDATAELEMENTDERIVATION)
  {
    //System.out.println("**Component Data Elements**");
    List<ComponentDataElementsListITEM> cadsrComponenentDataElementsList = cadsrDATAELEMENTDERIVATION
      .getComponentDataElementsList().getComponentDataElementsListITEM();
    ComponentDataElementsList cedarComponentDataElements = new ComponentDataElementsList();

    String componentDataElementsListNULLval = null;
    if (cadsrComponenentDataElementsList.isEmpty()) {
      componentDataElementsListNULLval = cadsrDATAELEMENTDERIVATION.getComponentDataElementsList().getNULL();
      if (componentDataElementsListNULLval == null) {
        componentDataElementsListNULLval = "NULL";
      } else if (componentDataElementsListNULLval.equals("TRUE")) {
        componentDataElementsListNULLval = "NULL";
      }
      //System.out.println(componenentDataElementsListNULLval);
      //cedarComponentDataElements.setComponentDataElementsListItem(componentDataElementsListNULLval); //TODO figure out
    } else {
      for (ComponentDataElementsListITEM val : cadsrComponenentDataElementsList) {
        //component data elements public id
        String cadsrComponentDataElementsListItemPublicID = val.getPublicId().getContent();
        //System.out.println(cadsrComponentDataElementsListItemPublicID);

        //component data elements long name
        String cadsrComponentDataElementsListItemLongName = val.getLongName().getContent();
        //System.out.println(cadsrComponentDataElementsListItemLongName);

        //component data elements preferred name
        String cadsrComponentDataElementsListItemPreferredName = val.getPreferredName().getContent();
        //System.out.println(cadsrComponentDataElementsListItemPreferredName);

        //component data elements preferred definition
        String cadsrComponentDataElementsListItemPreferredDefinition = val.getPreferredDefinition().getContent();
        //System.out.println(cadsrComponentDataElementsListItemPreferredDefinition);

        //component data elements version
        String cadsrComponentDataElementsListItemVersion = val.getVersion().getContent();
        //System.out.println(cadsrComponentDataElementsListItemVersion);

        //component data elements workflow status
        String cadsrComponentDataElementsListItemWorkflowStatus = val.getWorkflowStatus().getContent();
        //System.out.println(cadsrComponentDataElementsListItemWorkflowStatus);
        //component data elements context name

        String cadsrComponentDataElementsListItemContextName = val.getContextName().getContent();
        //System.out.println(cadsrComponentDataElementsListItemContextName);

        //component data elements display order
        String cadsrComponentDataElementsListItemDisplayOrder = val.getDisplayOrder().getContent();
        //System.out.println(cadsrComponentDataElementsListItemDisplayOrder);
      }
    }
  }
}

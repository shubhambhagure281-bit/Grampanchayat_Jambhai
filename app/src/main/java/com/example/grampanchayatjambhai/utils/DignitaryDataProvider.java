package com.example.grampanchayatjambhai.utils;

import com.example.grampanchayatjambhai.R;
import com.example.grampanchayatjambhai.models.Dignitary;

import java.util.ArrayList;
import java.util.List;

public class DignitaryDataProvider {

    public static List<Dignitary> getDignitariesForLocation(String district, String taluka, String gramPanchayat) {
        List<Dignitary> list = new ArrayList<>();
        long now = System.currentTimeMillis();

        String currentDistrict = (district != null && !district.isEmpty()) ? district : "छत्रपती संभाजीनगर";
        String currentTaluka = (taluka != null && !taluka.isEmpty()) ? taluka : "सिल्लोड";
        String currentGP = (gramPanchayat != null && !gramPanchayat.isEmpty()) ? gramPanchayat : "जांभई";

        // SECTION 1: STATE LEVEL (महाराष्ट्र राज्य)
        list.add(new Dignitary(
                "DIG-001",
                "मा. श्री. देवेंद्र फडणवीस",
                "मुख्यमंत्री, महाराष्ट्र राज्य",
                "STATE",
                "महाराष्ट्र",
                "-",
                "-",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d2/Devendra_Fadnavis_2023.jpg/480px-Devendra_Fadnavis_2023.jpg",
                R.drawable.cm,
                "https://maharashtra.gov.in/",
                "महाराष्ट्र शासन अधिकृत संकेतस्थळ",
                now,
                "सक्रिय"
        ));

        list.add(new Dignitary(
                "DIG-002",
                "मा. श्री. एकनाथ शिंदे",
                "उपमुख्यमंत्री, महाराष्ट्र राज्य",
                "STATE",
                "महाराष्ट्र",
                "-",
                "-",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/Eknath_Shinde_in_2022.jpg/480px-Eknath_Shinde_in_2022.jpg",
                R.drawable.eknath_shinde,
                "https://maharashtra.gov.in/",
                "महाराष्ट्र शासन अधिकृत संकेतस्थळ",
                now,
                "सक्रिय"
        ));

        list.add(new Dignitary(
                "DIG-003",
                "मा. श्रीमती सुनेत्रा अजित पवार",
                "उपमुख्यमंत्री, महाराष्ट्र राज्य",
                "STATE",
                "महाराष्ट्र",
                "-",
                "-",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a2/Sunetra_Pawar.jpg/480px-Sunetra_Pawar.jpg",
                R.drawable.sunetra_pawar,
                "https://maharashtra.gov.in/",
                "महाराष्ट्र शासन अधिकृत संकेतस्थळ",
                now,
                "सक्रिय"
        ));

        list.add(new Dignitary(
                "DIG-004",
                "मा. श्री. जयकुमार गोरे",
                "मंत्री, ग्रामविकास व पंचायतराज विभाग",
                "STATE",
                "महाराष्ट्र",
                "-",
                "-",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/2/23/Jaykumar_Gore.jpg/480px-Jaykumar_Gore.jpg",
                R.drawable.jaykumar_gore,
                "https://rdd.maharashtra.gov.in/",
                "ग्रामविकास विभाग महाराष्ट्र शासन",
                now,
                "सक्रिय"
        ));

        list.add(new Dignitary(
                "DIG-005",
                "मा. श्री. योगेश कदम",
                "राज्यमंत्री, ग्रामविकास व पंचायतराज विभाग",
                "STATE",
                "महाराष्ट्र",
                "-",
                "-",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/Yogesh_Kadam.jpg/480px-Yogesh_Kadam.jpg",
                R.drawable.dignitary_kadam,
                "https://rdd.maharashtra.gov.in/",
                "ग्रामविकास विभाग महाराष्ट्र शासन",
                now,
                "सक्रिय"
        ));

        // SECTION 2: GRAMVIKAS & PANCHAYAT RAJ DEPARTMENT
        list.add(new Dignitary(
                "DIG-006",
                "डॉ. चंद्रकांत पुलकुंडवार",
                "सचिव, ग्रामविकास व पंचायतराज विभाग",
                "STATE",
                "महाराष्ट्र",
                "-",
                "-",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c5/Chandrakant_Pulkundwar.jpg/480px-Chandrakant_Pulkundwar.jpg",
                R.drawable.dignitary_pulkundwar,
                "https://rdd.maharashtra.gov.in/",
                "ग्रामविकास विभाग महाराष्ट्र शासन",
                now,
                "सक्रिय"
        ));

        // SECTION 3: DISTRICT LEVEL (जिल्हास्तर)
        list.add(new Dignitary(
                "DIG-007",
                "मा. श्री. संजय शिरसाट",
                "पालकमंत्री",
                "DISTRICT",
                currentDistrict,
                "-",
                "-",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/36/Sanjay_Shirsat.jpg/480px-Sanjay_Shirsat.jpg",
                R.drawable.sanjay_shirsat,
                "https://chhatrapatisambhajinagar.gov.in/",
                "जिल्हा प्रशासन अधिकृत संकेतस्थळ",
                now,
                "सक्रिय"
        ));

        list.add(new Dignitary(
                "DIG-008",
                "श्री. अविनाश गलांडे",
                "अध्यक्ष, जिल्हा परिषद",
                "DISTRICT",
                currentDistrict,
                "-",
                "-",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7c/Avinash_Galande.jpg/480px-Avinash_Galande.jpg",
                R.drawable.dignitary_galande,
                "https://zpchhatrapatisambhajinagar.maharashtra.gov.in/",
                "जिल्हा परिषद अधिकृत संकेतस्थळ",
                now,
                "सक्रिय"
        ));

        list.add(new Dignitary(
                "DIG-009",
                "श्रीमती मिन्नू पी. एम. (IAS)",
                "मुख्य कार्यकारी अधिकारी, जिल्हा परिषद",
                "DISTRICT",
                currentDistrict,
                "-",
                "-",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/2/22/Minnu_PM_IAS.jpg/480px-Minnu_PM_IAS.jpg",
                R.drawable.dignitary_minnu,
                "https://zpchhatrapatisambhajinagar.maharashtra.gov.in/",
                "जिल्हा परिषद अधिकृत संकेतस्थळ",
                now,
                "सक्रिय"
        ));

        // SECTION 4: TALUKA LEVEL (पंचायत समिती / तालुका स्तर)
        list.add(new Dignitary(
                "DIG-010",
                "श्रीमती अनिता बनकर",
                "सभापती, पंचायत समिती",
                "TALUKA",
                currentDistrict,
                currentTaluka,
                "-",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/Panchayat_Sabhapati.jpg/480px-Panchayat_Sabhapati.jpg",
                R.drawable.dignitary_bankar,
                "https://zpchhatrapatisambhajinagar.maharashtra.gov.in/",
                "पंचायत समिती अधिकृत संकेतस्थळ",
                now,
                "सक्रिय"
        ));

        list.add(new Dignitary(
                "DIG-011",
                "श्री. रत्नाकर पगार",
                "गटविकास अधिकारी, पंचायत समिती",
                "TALUKA",
                currentDistrict,
                currentTaluka,
                "-",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/8/88/BDO_Officer.jpg/480px-BDO_Officer.jpg",
                R.drawable.dignitary_pagar,
                "https://zpchhatrapatisambhajinagar.maharashtra.gov.in/",
                "पंचायत समिती अधिकृत संकेतस्थळ",
                now,
                "सक्रिय"
        ));

        // SECTION 5: GRAM PANCHAYAT LEVEL (ग्रामपंचायत स्तर)
        list.add(new Dignitary(
                "DIG-012",
                "मा. सौ. लक्ष्मीबाई नारायण शिंदे",
                "सरपंच, ग्रामपंचायत " + currentGP,
                "GRAM_PANCHAYAT",
                currentDistrict,
                currentTaluka,
                currentGP,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Sarpanch_Panchayat.jpg/480px-Sarpanch_Panchayat.jpg",
                R.drawable.dignitary_laxmibai,
                "ग्रामपंचायत कार्यालय जांभई अधिकृत नोंद",
                "ग्रामपंचायत अधिकृत दप्तर नोंद",
                now,
                "सक्रिय"
        ));

        list.add(new Dignitary(
                "DIG-013",
                "श्री. नितीन गुल्हाने",
                "ग्रामविकास अधिकारी / ग्रामसेवक, ग्रामपंचायत " + currentGP,
                "GRAM_PANCHAYAT",
                currentDistrict,
                currentTaluka,
                currentGP,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/5/52/Gramsevak_Officer.jpg/480px-Gramsevak_Officer.jpg",
                R.drawable.dignitary_gulhane,
                "ग्रामपंचायत कार्यालय जांभई अधिकृत नोंद",
                "ग्रामपंचायत अधिकृत दप्तर नोंद",
                now,
                "सक्रिय"
        ));

        return list;
    }
}
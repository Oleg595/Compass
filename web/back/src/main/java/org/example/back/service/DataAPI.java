package org.example.back.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DataAPI {
    public List<String> getCriterias() {
        return List.of(
            "Описание", "Категория", "Сайт", "Количество персонала", "Год основания",
            "Сумма инвестиций в $", "Оценочная стоимость", "Количество инвесторов");
    }

    public List<Map<String, String>> getAlternatives() {
        return List.of(
            Map.of(
                "id", "1",
                "name", "Capchase",
                "Описание", "A full year of recurring revenue of your bank. On day one.",
                "Категория", "FinTech",
                "Сайт", "http://capchase.com/",
                "Количество персонала", "11-50",
                "Год основания", "2020",
                "Сумма инвестиций в $", "949600000",
                "Оценочная стоимость", "-",
                "Количество инвесторов", "8"
            ),
            Map.of(
                "id", "2",
                "name", "Classplus",
                "Описание", "Classplus was born out of an urge to redefine classroom engagement in this country.",
                "Категория", "EdTech",
                "Сайт", "https://classplusapp.com/",
                "Количество персонала", "501-1000",
                "Год основания", "2018",
                "Сумма инвестиций в $", "211500000",
                "Оценочная стоимость", "600000000",
                "Количество инвесторов", "3"
            ),
            Map.of(
                "id", "3",
                "name", "Pipe",
                "Описание", "Grow on your terms.",
                "Категория", "FinTech",
                "Сайт", "https://pipe.com/",
                "Количество персонала", "11-50",
                "Год основания", "2019",
                "Сумма инвестиций в $", "366500000",
                "Оценочная стоимость", "2000000000",
                "Количество инвесторов", "23"
            ),
            Map.of(
                "id", "4",
                "name", "Codenotary",
                "Описание", "Secure your sensitive data by creating immutable ledger proofs of existence, notarizations, and s...",
                "Категория", "Cybersecurity",
                "Сайт", "https://www.vchain.us/",
                "Количество персонала", "11-50",
                "Год основания", "2018",
                "Сумма инвестиций в $", "29500000",
                "Оценочная стоимость", "-",
                "Количество инвесторов", "1"
            ),
            Map.of(
                "id", "5",
                "name", "Deliverect",
                "Описание", "Deliverect connects businesses with their customers through online food delivery",
                "Категория", "FoodTech",
                "Сайт", "https://deliverect.com/",
                "Количество персонала", "201-500",
                "Год основания", "2018",
                "Сумма инвестиций в $", "236361005",
                "Оценочная стоимость", "-",
                "Количество инвесторов", "2"
            ),
            Map.of(
                "id", "6",
                "name", "AgentSync.",
                "Описание", "AgentSync automatically enforces state producer licensing and appointment regulatory requirements...",
                "Категория", "Automation",
                "Сайт", "https://agentsync.io/",
                "Количество персонала", "-",
                "Год основания", "2018",
                "Сумма инвестиций в $", "111100000",
                "Оценочная стоимость", "-",
                "Количество инвесторов", "6"
            ),
            Map.of(
                "id", "7",
                "name", "Ahana",
                "Описание", "Ahana is the Presto company that offers a managed service for Presto on AWS.",
                "Категория", "Cloud",
                "Сайт", "https://ahana.io/",
                "Количество персонала", "-",
                "Год основания", "2020",
                "Сумма инвестиций в $", "720000",
                "Оценочная стоимость", "-",
                "Количество инвесторов", "3"
            ),
            Map.of(
                "id", "8",
                "name", "Vendr",
                "Описание", "Vendr is forever changing how companies buy and renew SaaS.",
                "Категория", "IT Platform SaaS Service Software",
                "Сайт", "http://www.vendr.com/",
                "Количество персонала", "51-200",
                "Год основания", "2018",
                "Сумма инвестиций в $", "216000000",
                "Оценочная стоимость", "1000000000",
                "Количество инвесторов", "3"
            ),
            Map.of(
                "id", "9",
                "name", "SeedBlink",
                "Описание", "Seedblink is European fastest growing investing platform specialized in sourcing, " +
                    "vetting, financ...",
                "Категория", "FinTech",
                "Сайт", "http://www.seedblink.com/",
                "Количество персонала", "-",
                "Год основания", "2019",
                "Сумма инвестиций в $", "48008330",
                "Оценочная стоимость", "-",
                "Количество инвесторов", "1"
            ),
            Map.of(
                "id", "10",
                "name", "Arc Technologies",
                "Описание", "Arc is a digitally-native financial services platform that leverages technology to reduce the tim...",
                "Категория", "FinTech",
                "Сайт", "https://www.arc.tech/",
                "Количество персонала", "11-50",
                "Год основания", "2021",
                "Сумма инвестиций в $", "181100000",
                "Оценочная стоимость", "-",
                "Количество инвесторов", "4"
            )
        );
    }
}

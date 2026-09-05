package ru.netology;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class AviaSoulsTest {
    private AviaSouls manager;
    private Ticket ticket1;
    private Ticket ticket2;
    private Ticket ticket3;
    private Ticket ticket4;
    private Ticket ticket5;
    private Ticket ticket6;

    @BeforeEach
    public void setUp() {
        manager = new AviaSouls();

        // Создаем билеты для маршрута Москва -> Сочи
        ticket1 = new Ticket("Москва", "Сочи", 15000, 800, 1100);  // 300 мин
        ticket2 = new Ticket("Москва", "Сочи", 12000, 900, 1200);  // 300 мин
        ticket3 = new Ticket("Москва", "Сочи", 18000, 1000, 1230); // 230 мин
        ticket4 = new Ticket("Москва", "Сочи", 10000, 700, 1100);  // 400 мин

        // Билет для маршрута Москва -> СПб
        ticket5 = new Ticket("Москва", "СПб", 8000, 800, 1000);    // 200 мин

        // Билет для маршрута Казань -> Сочи
        ticket6 = new Ticket("Казань", "Сочи", 9000, 900, 1200);

        manager.add(ticket1);
        manager.add(ticket2);
        manager.add(ticket3);
        manager.add(ticket4);
        manager.add(ticket5);
        manager.add(ticket6);
    }

    // ===== ТЕСТЫ ДЛЯ COMPARETO (СРАВНЕНИЕ ПО ЦЕНЕ) =====

    @Test
    public void shouldCompareTicketsByPrice() {
        Assertions.assertTrue(ticket4.compareTo(ticket1) < 0);
        Assertions.assertTrue(ticket1.compareTo(ticket3) < 0);
        Assertions.assertTrue(ticket3.compareTo(ticket2) > 0);

        Ticket ticketSamePrice = new Ticket("Москва", "Сочи", 15000, 800, 1100);
        Assertions.assertEquals(0, ticket1.compareTo(ticketSamePrice));
    }

    @Test
    public void shouldSortTicketsByPriceUsingSearch() {
        Ticket[] expected = {ticket4, ticket2, ticket1, ticket3};
        Ticket[] actual = manager.search("Москва", "Сочи");
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldSortTicketsByPriceWithDifferentPrices() {
        Ticket ticket7 = new Ticket("Москва", "Сочи", 5000, 600, 1000);
        manager.add(ticket7);

        Ticket[] expected = {ticket7, ticket4, ticket2, ticket1, ticket3};
        Ticket[] actual = manager.search("Москва", "Сочи");
        Assertions.assertArrayEquals(expected, actual);
    }

    // ===== ТЕСТЫ ДЛЯ КОМПАРАТОРА ПО ВРЕМЕНИ =====

    @Test
    public void shouldCompareTicketsByTime() {
        TicketTimeComparator comparator = new TicketTimeComparator();

        Assertions.assertTrue(comparator.compare(ticket3, ticket1) < 0);
        Assertions.assertTrue(comparator.compare(ticket2, ticket4) < 0);
        Assertions.assertTrue(comparator.compare(ticket1, ticket3) > 0);

        Ticket ticketSameTime = new Ticket("Москва", "Сочи", 20000, 800, 1100);
        Assertions.assertEquals(0, comparator.compare(ticket1, ticketSameTime));
    }

    @Test
    public void shouldSortTicketsByTimeUsingComparator() {
        TicketTimeComparator comparator = new TicketTimeComparator();

        Ticket[] expected = {ticket3, ticket1, ticket2, ticket4};
        Ticket[] actual = manager.searchAndSortBy("Москва", "Сочи", comparator);
        Assertions.assertArrayEquals(expected, actual);
    }

    // ===== ТЕСТЫ ДЛЯ ПОИСКА =====

    @Test
    public void shouldSearchTicketsByRoute() {
        Ticket[] expected = {ticket4, ticket2, ticket1, ticket3};
        Ticket[] actual = manager.search("Москва", "Сочи");
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldReturnEmptyArrayWhenNoTicketsFound() {
        Ticket[] expected = {};
        Ticket[] actual = manager.search("Москва", "Лондон");
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldSearchAndSortByPrice() {
        Ticket[] expected = {ticket4, ticket2, ticket1, ticket3};
        Ticket[] actual = manager.searchAndSortBy("Москва", "Сочи", (t1, t2) ->
                Integer.compare(t1.getPrice(), t2.getPrice()));
        Assertions.assertArrayEquals(expected, actual);
    }

    // ===== ИСПРАВЛЕННЫЙ ТЕСТ (СТРОКА 219) =====
    @Test
    public void shouldSortByPriceWithSamePrices() {
        // Создаём билеты с одинаковой ценой (15000)
        Ticket ticket7 = new Ticket("Москва", "Сочи", 15000, 600, 900);
        Ticket ticket8 = new Ticket("Москва", "Сочи", 15000, 700, 1000);
        manager.add(ticket7);
        manager.add(ticket8);

        // Ожидаемый порядок: по возрастанию цены
        // 10000 (ticket4), 12000 (ticket2), 15000 (ticket1, ticket7, ticket8), 18000 (ticket3)
        // ticket1, ticket7, ticket8 - все с ценой 15000, порядок соответствует добавлению
        Ticket[] expected = {ticket4, ticket2, ticket1, ticket7, ticket8, ticket3};
        Ticket[] actual = manager.search("Москва", "Сочи");

        Assertions.assertArrayEquals(expected, actual);
    }

    // ===== ИСПРАВЛЕННЫЙ ТЕСТ =====
    @Test
    public void shouldSortByFlightTimeWithSameTimeDifferentPrices() {
        TicketTimeComparator comparator = new TicketTimeComparator();

        Ticket ticket7 = new Ticket("Москва", "Сочи", 20000, 800, 1100); // 300 мин
        Ticket ticket8 = new Ticket("Москва", "Сочи", 5000, 800, 1100);  // 300 мин
        manager.add(ticket7);
        manager.add(ticket8);

        // 230 мин (ticket3), 300 мин (ticket1, ticket2, ticket7, ticket8), 400 мин (ticket4)
        Ticket[] expected = {ticket3, ticket1, ticket2, ticket7, ticket8, ticket4};
        Ticket[] actual = manager.searchAndSortBy("Москва", "Сочи", comparator);

        Assertions.assertArrayEquals(expected, actual);
    }

    // ===== ИСПРАВЛЕННЫЙ ТЕСТ =====
    @Test
    public void shouldSortTicketsByTimeWithDifferentTimes() {
        TicketTimeComparator comparator = new TicketTimeComparator();

        // ticket7: 230 мин (такое же как у ticket3)
        Ticket ticket7 = new Ticket("Москва", "Сочи", 25000, 800, 1030); // 230 мин
        manager.add(ticket7);

        // Ожидаемый порядок по времени:
        // 230 мин (ticket3, ticket7), 300 мин (ticket1, ticket2), 400 мин (ticket4)
        // ticket3 и ticket7 - оба 230 мин, порядок соответствует добавлению
        Ticket[] expected = {ticket3, ticket7, ticket1, ticket2, ticket4};
        Ticket[] actual = manager.searchAndSortBy("Москва", "Сочи", comparator);

        Assertions.assertArrayEquals(expected, actual);
    }

    // ===== ДОПОЛНИТЕЛЬНЫЕ ТЕСТЫ =====

    @Test
    public void shouldFindAllTickets() {
        Ticket[] expected = {ticket1, ticket2, ticket3, ticket4, ticket5, ticket6};
        Ticket[] actual = manager.findAll();
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldSearchWithOneTicket() {
        AviaSouls emptyManager = new AviaSouls();
        Ticket ticket = new Ticket("Москва", "Сочи", 15000, 800, 1100);
        emptyManager.add(ticket);

        Ticket[] expected = {ticket};
        Ticket[] actual = emptyManager.search("Москва", "Сочи");
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldSearchAndSortWithEmptyResult() {
        TicketTimeComparator comparator = new TicketTimeComparator();
        Ticket[] expected = {};
        Ticket[] actual = manager.searchAndSortBy("Москва", "Лондон", comparator);
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    public void shouldCompareByPriceAndTimeWorkTogether() {
        TicketTimeComparator timeComparator = new TicketTimeComparator();

        Ticket ticket7 = new Ticket("Москва", "Сочи", 100, 800, 1000); // 200 мин
        Ticket ticket8 = new Ticket("Москва", "Сочи", 1000, 800, 900); // 100 мин
        manager.add(ticket7);
        manager.add(ticket8);

        // Ожидаемый порядок по времени:
        // 100 мин (ticket8), 200 мин (ticket7), 230 мин (ticket3), 300 мин (ticket1, ticket2), 400 мин (ticket4)
        Ticket[] expected = {ticket8, ticket7, ticket3, ticket1, ticket2, ticket4};
        Ticket[] actual = manager.searchAndSortBy("Москва", "Сочи", timeComparator);
        Assertions.assertArrayEquals(expected, actual);
    }

    // ===== ДОПОЛНИТЕЛЬНЫЙ ТЕСТ ДЛЯ РАЗНЫХ МАРШРУТОВ =====
    @Test
    public void shouldSearchAndSortByTimeWithDifferentRoute() {
        TicketTimeComparator comparator = new TicketTimeComparator();

        // В setUp() уже есть ticket5 для маршрута "Москва" -> "СПб"
        // Добавляем еще 2 билета для этого маршрута
        Ticket ticket7 = new Ticket("Москва", "СПб", 10000, 900, 1100); // 200 мин
        Ticket ticket8 = new Ticket("Москва", "СПб", 11000, 700, 1000); // 300 мин
        manager.add(ticket7);
        manager.add(ticket8);

        // Все билеты для "Москва" -> "СПб":
        // ticket5: 200 мин, ticket7: 200 мин, ticket8: 300 мин
        Ticket[] expected = {ticket5, ticket7, ticket8};
        Ticket[] actual = manager.searchAndSortBy("Москва", "СПб", comparator);
        Assertions.assertArrayEquals(expected, actual);
    }
}
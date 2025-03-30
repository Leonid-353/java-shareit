package ru.practicum.shareit.utils;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@Profile("test")
@SpringBootTest
public abstract class BaseTestServiceContext {
}

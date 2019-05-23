package com.example.demo.model;

import lombok.Data;

/**
 * Un dto wrappant une entité et une action de crud de type T.
 *
 * @param <T>
 */
@Data
public class CrudAction<T> {

    private T entity;
    private CrudEnum action;

    public CrudAction(T entity, CrudEnum action) {
        this.entity = entity;
        this.action = action;
    }
}

export interface Todo {
  id: number;
  content: string;
}

export interface Meta {
  totalCount: number;
}

/**
 * Критерий поиска оборудования
 */
export interface Criteria {
  /**
   * Инвентарный номер
   */
  invNumber: string;
  /**
   * id ответственного
   */
  personId: number;
  /**
   * id состояния
   */
  stateId: number;
  /**
   * id типа оборудования
   */
  typeId: number;
  /**
   * Серийный номер
   */
  serial: string;
  /**
   * Комментарий
   */
  comment: string;
  /**
   * Дата покупки
   */
  purchaseDate: string | null;
  /**
   * ???
   */
  displayDate: string | null;
}

/**
 * результат поиска оборудования (для вывода в таблице)
 */
export interface SearchResult {
  guid: string;

  /**
   * инвентарник
   */
  invNumber: string | null;

  /**
   * Серийник
   */
  serial: string | null;

  /**
   * Тип оборудования
   */
  type: string | null;

  /**
   * состояние оборудования
   */
  state: string | null;

  /**
   * Ответственный
   */
  person: string | null;

  /**
   * Комментарий
   */
  comment: string | null;

  /**
   * Дата покупки
   */
  purchaseDate: Date | null;
}

/**
 * Элемент справочника.
 */
export interface Reference {
  /**
   * id
   */
  id: number;

  /**
   * Название
   */
  name: string;
}

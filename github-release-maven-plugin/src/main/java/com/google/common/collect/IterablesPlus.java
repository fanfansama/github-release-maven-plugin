package com.google.common.collect;

/**
 * @author fberthault
 * @since 1.0
 * Classe ajoutant des fonctionalités à la classe de Guava {@link Iterables}.
 */
public final class IterablesPlus {

	/**
	 * Permet d'éviter d'avoir à re-spécifier la valeur {@code null} par défaut à chaque fois.
	 *
	 * @param iterable
	 *            itérable pour lequel renvoyer le premier élément
	 * @return le premier élément de {@code iterable} si {@code iterable} n'est pas vide ou {@code null}, {@code null} le cas échéant
	 */
	public static <T> T getFirst(Iterable<T> iterable) {
		if (iterable != null) {
			return Iterables.getFirst(iterable, null);
		} else {
			return null;
		}
	}

}
import java.awt.*;
import java.util.Iterator;


public class MovieDB {
	private MyLinkedList<Genre> genres = new MyLinkedList<>();

    public MovieDB() {
    }

    public void insert(MovieDBItem item) {
		Genre genre = genres.find(new Genre(item.getGenre()));

		if(genre == null) {
			Genre newGenre = new Genre(item.getGenre());
			genres.insertionSort(newGenre);
			MyLinkedList<String> list = new MyLinkedList<>();
			list.add(item.getTitle());
			newGenre.movieList = list;
		} else {
			genre.movieList.insertionSort(item.getTitle());
		}
    }

    public void delete(MovieDBItem item) {
		Genre genre = genres.find(new Genre(item.getGenre()));

		if(genre != null) {
			genre.movieList.remove(item.getTitle());

			if(genre.movieList.isEmpty()) {
				genres.remove(genre);
			}
		}
    }

    public MyLinkedList<MovieDBItem> search(String term) {
        MyLinkedList<MovieDBItem> results = new MyLinkedList<MovieDBItem>();

		for(Genre genre : genres) {
			for(String title: genre.movieList) {
				if(title.contains(term)) {
					results.add(new MovieDBItem(genre.getItem(), title));
				}
			}
		}
        return results;
    }
    
    public MyLinkedList<MovieDBItem> items() {
        MyLinkedList<MovieDBItem> results = new MyLinkedList<MovieDBItem>();

		for(Genre genre : genres) {
			for(String title : genre.movieList) {
				results.add(new MovieDBItem(genre.getItem(), title));
			}
		}

    	return results;
    }
}

class Genre extends Node<String> implements Comparable<Genre> {
	public MyLinkedList<String> movieList = new MyLinkedList<>();

	public Genre(String name) {
		super(name);
	}
	
	@Override
	public int compareTo(Genre o) {
		return this.getItem().compareTo(o.getItem());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if(getClass() != obj.getClass()) return false;

		Genre other = (Genre) obj;
		if(this.getItem() == null) {
            return other.getItem() == null;
		} else return this.getItem().equals(other.getItem());
    }
}

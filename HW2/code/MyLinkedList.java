
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyLinkedList<T extends Comparable<T>> implements ListInterface<T> {
	Node<T> head;
	int numItems;

	public MyLinkedList() {
		head = new Node<T>(null);
	}

	public final Iterator<T> iterator() {
		return new MyLinkedListIterator<T>(this);
	}

	public void insertionSort(T item) {
		if (this.isEmpty()) {
			this.add(item);
			return;
		}

		Node<T> current = this.head.getNext();
		Node<T> prev = this.head;

		while (current != null) {
			T currentItem = current.getItem();

			if (currentItem.equals(item)) {
				return;
			}

			if (currentItem.compareTo(item) > 0) {
				break;
			}

			prev = current;
			current = current.getNext();
		}

		prev.insertNext(item);
		numItems++;
	}

	public T find(T item) {
		if (this.isEmpty()) { return null; }

		for(T temp: this){
			if(temp.equals(item)) { return temp; }
		}

		return null;
	}

	@Override
	public boolean isEmpty() {
		return head.getNext() == null;
	}

	@Override
	public int size() {
		return numItems;
	}

	@Override
	public T first() {
		if (this.isEmpty()) throw new NoSuchElementException();
		return head.getNext().getItem();
	}

	@Override
	public void add(T item) {
		Node<T> last = head;
		while (last.getNext() != null) {
			last = last.getNext();
		}
		last.insertNext(item);
		numItems += 1;
	}

	@Override
	public void removeAll() {
		head.setNext(null);
		numItems = 0;
	}

	public void remove(T item){
		Node<T> current = head.getNext();
		Node<T> prev = head;

		while(current != null){
			if(current.getItem().equals(item)){
				prev.removeNext();
				numItems--;
				return;
			}

			prev = current;
			current = current.getNext();
		}
	}
}

class MyLinkedListIterator<T extends Comparable<T>> implements Iterator<T> {
	private MyLinkedList<T> list;
	private Node<T> curr;
	private Node<T> prev;

	public MyLinkedListIterator(MyLinkedList<T> list) {
		this.list = list;
		this.curr = list.head;
		this.prev = null;
	}

	@Override
	public boolean hasNext() {
		return curr.getNext() != null;
	}

	@Override
	public T next() {
		if (!hasNext())
			throw new NoSuchElementException();

		prev = curr;
		curr = curr.getNext();

		return curr.getItem();
	}

	@Override
	public void remove() {
		if (prev == null)
			throw new IllegalStateException("next() should be called first");
		if (curr == null)
			throw new NoSuchElementException();
		prev.removeNext();
		list.numItems -= 1;
		curr = prev;
		prev = null;
	}

}
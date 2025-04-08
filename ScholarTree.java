import java.util.ArrayList;

public class ScholarTree {

	public ScholarNode primaryRoot;		//root of the primary B+ tree
	public ScholarNode secondaryRoot;	//root of the secondary B+ tree
	public ScholarTree(Integer order) {
		ScholarNode.order = order;
		primaryRoot = new ScholarNodePrimaryLeaf(null);
		primaryRoot.level = 0;
		secondaryRoot = new ScholarNodeSecondaryLeaf(null);
		secondaryRoot.level = 0;
	}

	public void addPaper(CengPaper paper) {
		// TODO: Implement this method

		// Primary add

		// Root is leaf
		if (primaryRoot.getType() == ScholarNodeType.Leaf) {
			// Root is empty
			ScholarNodePrimaryLeaf temp = (ScholarNodePrimaryLeaf) primaryRoot;

			if (temp.paperCount() == 0) {
				temp.addPaper(0, paper);
				primaryRoot = temp;
			}
			// Root is not empty but it size less then order times 2
			else if (temp.paperCount() < (2 * ScholarNode.order)) {

				Integer index = 0;
				while (index < temp.paperCount() && paper.paperId() > temp.paperIdAtIndex(index)) {
					index++;
				}

				temp.addPaper(index, paper);
				primaryRoot = temp;
			}
			// Root is full
			else {
				ScholarNodePrimaryIndex rootTemp = new ScholarNodePrimaryIndex(null);
				ArrayList<CengPaper> leftPapers = new ArrayList<CengPaper>();
				ArrayList<CengPaper> rightPapers = new ArrayList<CengPaper>();
				ArrayList<CengPaper> papers = temp.getPapers();

				Integer index = 0;
				while (index < temp.paperCount() && paper.paperId() > temp.paperIdAtIndex(index)) {
					index++;
				}

				temp.addPaper(index, paper);

				index = 0;
				while (index < ScholarNode.order) {
					leftPapers.add(papers.get(index));
					index++;
				}
				while (index < temp.paperCount()) {
					rightPapers.add(papers.get(index));
					index++;
				}

				ScholarNodePrimaryLeaf leftNode = new ScholarNodePrimaryLeaf(rootTemp, leftPapers);
				ScholarNodePrimaryLeaf rightNode = new ScholarNodePrimaryLeaf(rootTemp, rightPapers);

				rootTemp.addChild(0, leftNode);
				rootTemp.addChild(1, rightNode);
				rootTemp.addPaperId(0, rightPapers.get(0).paperId());

				primaryRoot = rootTemp;
			}
		}
		// Root is not a leaf
		else {
			ScholarNode temp = primaryRoot;

			while (temp.getType() == ScholarNodeType.Internal) {

				ScholarNodePrimaryIndex sr = (ScholarNodePrimaryIndex) temp;

				ArrayList<Integer> Ids = sr.getPaperIds();
				Integer index = 0;
				Integer paperId = paper.paperId();

				while (index < Ids.size() && paperId >= Ids.get(index)) {
					index++;
				}

				temp = sr.getChildrenAt(index);

			}

			ScholarNodePrimaryLeaf md = (ScholarNodePrimaryLeaf) temp;

			// Leaf is not full
			if (md.paperCount() < (2 * ScholarNode.order)) {

				Integer index = 0;
				while (index < md.paperCount() && paper.paperId() > md.paperIdAtIndex(index)) {
					index++;
				}

				md.addPaper(index, paper);

			}
			// Leaf is full
			else {

				ArrayList<CengPaper> leftPapers = new ArrayList<CengPaper>();
				ArrayList<CengPaper> rightPapers = new ArrayList<CengPaper>();
				ArrayList<CengPaper> papers = md.getPapers();

				Integer index = 0;
				while (index < md.paperCount() && paper.paperId() > md.paperIdAtIndex(index)) {
					index++;
				}

				md.addPaper(index, paper);

				index = 0;
				while (index < ScholarNode.order) {
					leftPapers.add(papers.get(index));
					index++;
				}
				while (index < md.paperCount()) {
					rightPapers.add(papers.get(index));
					index++;
				}

				ScholarNodePrimaryIndex parent = (ScholarNodePrimaryIndex) md.getParent();

				ArrayList<Integer> Ids = parent.getPaperIds();
				Integer ind = 0;
				Integer paperId = paper.paperId();

				while (ind < Ids.size() && paperId >= Ids.get(ind)) {
					ind++;
				}

				ScholarNodePrimaryLeaf leftNode = new ScholarNodePrimaryLeaf(parent, leftPapers);
				ScholarNodePrimaryLeaf rightNode = new ScholarNodePrimaryLeaf(parent, rightPapers);

				parent.addChild(ind, leftNode);
				parent.getAllChildren().set((ind + 1), rightNode);
				parent.addPaperId(ind, rightPapers.get(0).paperId());

				while (parent.paperIdCount() > (ScholarNode.order * 2)) {
					// Root
					if (parent.getParent() == null) {

						ArrayList<ScholarNode> children = parent.getAllChildren();
						ArrayList<ScholarNode> leftChildren = new ArrayList<ScholarNode>();
						ArrayList<ScholarNode> rightChildren = new ArrayList<ScholarNode>();

						ArrayList<Integer> pIds = parent.getPaperIds();
						ArrayList<Integer> leftPIds = new ArrayList<Integer>();
						ArrayList<Integer> rightPIds = new ArrayList<Integer>();

						index = 0;
						while (index < ScholarNode.order) {
							leftChildren.add(children.get(index));
							leftPIds.add(pIds.get(index));
							index++;
						}
						leftChildren.add(children.get(index));
						Integer rootn = pIds.get(index);
						index++;
						while (index < pIds.size()) {
							rightChildren.add(children.get(index));
							rightPIds.add(pIds.get(index));
							index++;
						}
						rightChildren.add(children.get(index));

						ScholarNodePrimaryIndex leftInd = new ScholarNodePrimaryIndex(primaryRoot, leftPIds, leftChildren);
						ScholarNodePrimaryIndex rightInd = new ScholarNodePrimaryIndex(primaryRoot, rightPIds, rightChildren);

						for (ScholarNode s : leftInd.getAllChildren()) {
							s.setParent(leftInd);
						}

						for (ScholarNode s : rightInd.getAllChildren()) {
							s.setParent(rightInd);
						}

						parent.getAllChildren().clear();
						parent.getPaperIds().clear();

						parent.addChild(0, leftInd);
						parent.addChild(1, rightInd);
						parent.addPaperId(0, rootn);

						primaryRoot = parent;

						leftInd.setParent(primaryRoot);
						rightInd.setParent(primaryRoot);

						break;
					}
					// Inner nodes

					else {

						ArrayList<ScholarNode> children = parent.getAllChildren();
						ArrayList<ScholarNode> leftChildren = new ArrayList<ScholarNode>();
						ArrayList<ScholarNode> rightChildren = new ArrayList<ScholarNode>();

						ArrayList<Integer> pIds = parent.getPaperIds();
						ArrayList<Integer> leftPIds = new ArrayList<Integer>();
						ArrayList<Integer> rightPIds = new ArrayList<Integer>();

						parent = (ScholarNodePrimaryIndex) parent.getParent();

						index = 0;
						while (index < ScholarNode.order) {
							leftChildren.add(children.get(index));
							leftPIds.add(pIds.get(index));
							index++;
						}
						leftChildren.add(children.get(index));
						Integer rootn = pIds.get(index);
						index++;
						while (index < pIds.size()) {
							rightChildren.add(children.get(index));
							rightPIds.add(pIds.get(index));
							index++;
						}
						rightChildren.add(children.get(index));

						ScholarNodePrimaryIndex leftInd = new ScholarNodePrimaryIndex(parent, leftPIds, leftChildren);
						ScholarNodePrimaryIndex rightInd = new ScholarNodePrimaryIndex(parent, rightPIds, rightChildren);

						for (ScholarNode s : leftInd.getAllChildren()) {
							s.setParent(leftInd);
						}

						for (ScholarNode s : rightInd.getAllChildren()) {
							s.setParent(rightInd);
						}

						Integer tt = pIds.get(ScholarNode.order);
						pIds = parent.getPaperIds();

						index = 0;
						while (index < pIds.size() && tt >= pIds.get(index)) {
							index++;
						}

						parent.addChild(index, leftInd);
						parent.getAllChildren().set((index + 1), rightInd);
						parent.addPaperId(index, rootn);

					}

				}

			}

		}

		// Secondary add

		// Root is leaf
		if (secondaryRoot.getType() == ScholarNodeType.Leaf) {
			// Root is empty
			ScholarNodeSecondaryLeaf temp = (ScholarNodeSecondaryLeaf) secondaryRoot;

			if (temp.journalCount() == 0) {
				temp.addPaper(0, paper);
				secondaryRoot = temp;
			}
			// Root is not empty but it's size less then order times 2
			else {

				Integer index = 0;
				while (index < temp.journalCount() && paper.journal().compareTo(temp.journalAtIndex(index)) > 0) {
					index++;
				}

				temp.addPaper(index, paper);

				secondaryRoot = temp;

				// Root is full now
				if (temp.journalCount() > (2 * ScholarNode.order)){

					ScholarNodeSecondaryIndex rootTemp = new ScholarNodeSecondaryIndex(null);

					ArrayList<String> leftJournal = new ArrayList<String>();
					ArrayList<String> rightJournal = new ArrayList<String >();
					ArrayList<String> jour = temp.getJournals();

					ArrayList<ArrayList<Integer>> leftChl = new ArrayList<ArrayList<Integer>>();
					ArrayList<ArrayList<Integer>> rightChl = new ArrayList<ArrayList<Integer>>();
					ArrayList<ArrayList<Integer>> chl = temp.getPaperIdBucket();

					index = 0;
					while (index < ScholarNode.order) {
						leftJournal.add(jour.get(index));
						leftChl.add(chl.get(index));
						index++;
					}
					while (index < jour.size()) {
						rightJournal.add(jour.get(index));
						rightChl.add(chl.get(index));
						index++;
					}

					ScholarNodeSecondaryLeaf leftNode = new ScholarNodeSecondaryLeaf(rootTemp, leftJournal, leftChl);
					ScholarNodeSecondaryLeaf rightNode = new ScholarNodeSecondaryLeaf(rootTemp, rightJournal, rightChl);

					rootTemp.addChild(0, leftNode);
					rootTemp.addChild(1, rightNode);
					rootTemp.addJournal(0, rightJournal.get(0));

					secondaryRoot = rootTemp;

				}

			}

		}

		// Root is not a leaf

		else {
			ScholarNode temp = secondaryRoot;

			while (temp.getType() == ScholarNodeType.Internal) {

				ScholarNodeSecondaryIndex sr = (ScholarNodeSecondaryIndex) temp;

				ArrayList<String> jours = sr.getJournals();
				Integer index = 0;
				String journal = paper.journal();

				while (index < jours.size() && journal.compareTo(jours.get(index)) >= 0) {
					index++;
				}

				temp = sr.getChildrenAt(index);

			}

			ScholarNodeSecondaryLeaf md = (ScholarNodeSecondaryLeaf) temp;

			Integer index = 0;
			while (index < md.journalCount() && paper.journal().compareTo(md.journalAtIndex(index)) > 0) {
				index++;
			}

			md.addPaper(index, paper);

			// leaf is full
			if (md.journalCount() > (2 * ScholarNode.order)){

				ArrayList<String> leftJournal = new ArrayList<String>();
				ArrayList<String> rightJournal = new ArrayList<String >();
				ArrayList<String> jour = md.getJournals();

				ArrayList<ArrayList<Integer>> leftChl = new ArrayList<ArrayList<Integer>>();
				ArrayList<ArrayList<Integer>> rightChl = new ArrayList<ArrayList<Integer>>();
				ArrayList<ArrayList<Integer>> chl = md.getPaperIdBucket();

				index = 0;
				while (index < ScholarNode.order) {
					leftJournal.add(jour.get(index));
					leftChl.add(chl.get(index));
					index++;
				}
				while (index < jour.size()) {
					rightJournal.add(jour.get(index));
					rightChl.add(chl.get(index));
					index++;
				}

				ScholarNodeSecondaryIndex parentTemp = (ScholarNodeSecondaryIndex) md.getParent();

				ScholarNodeSecondaryLeaf leftNode = new ScholarNodeSecondaryLeaf(parentTemp, leftJournal, leftChl);
				ScholarNodeSecondaryLeaf rightNode = new ScholarNodeSecondaryLeaf(parentTemp, rightJournal, rightChl);

				ArrayList<String> jours = parentTemp.getJournals();
				index = 0;
				String journal = paper.journal();

				while (index < jours.size() && journal.compareTo(jours.get(index)) >= 0) {
					index++;
				}

				parentTemp.addChild(index, leftNode);
				parentTemp.getAllChildren().set((index + 1), rightNode);
				parentTemp.addJournal(index, rightJournal.get(0));

				while (parentTemp.journalCount() > (2 * ScholarNode.order)){
					// Root
					if (parentTemp.getParent() == null) {

						ArrayList<String> leftJrl = new ArrayList<String>();
						ArrayList<String> rightJrl = new ArrayList<String >();
						ArrayList<String> jrl = parentTemp.getJournals();

						ArrayList<ScholarNode> leftCh = new ArrayList<ScholarNode>();
						ArrayList<ScholarNode> rightCh = new ArrayList<ScholarNode>();
						ArrayList<ScholarNode> perCh = parentTemp.getAllChildren();

						index = 0;

						while (index < ScholarNode.order) {
							leftJrl.add(jrl.get(index));
							leftCh.add(perCh.get(index));
							index++;
						}
						leftCh.add(perCh.get(index));
						String rn = jrl.get(index);
						index++;
						while (index < jrl.size()) {
							rightJrl.add(jrl.get(index));
							rightCh.add(perCh.get(index));
							index++;
						}
						rightCh.add(perCh.get(index));

						ScholarNodeSecondaryIndex leftNd = new ScholarNodeSecondaryIndex(secondaryRoot, leftJrl, leftCh);
						ScholarNodeSecondaryIndex rightNd = new ScholarNodeSecondaryIndex(secondaryRoot, rightJrl, rightCh);

						for (ScholarNode s : leftNd.getAllChildren()) {
							s.setParent(leftNd);
						}

						for (ScholarNode s : rightNd.getAllChildren()) {
							s.setParent(rightNd);
						}

						parentTemp.getAllChildren().clear();
						parentTemp.getJournals().clear();

						parentTemp.addChild(0, leftNd);
						parentTemp.addChild(1, rightNd);
						parentTemp.addJournal(0, rn);

						secondaryRoot = parentTemp;

						leftNd.setParent(secondaryRoot);
						rightNd.setParent(secondaryRoot);

						break;
					}

					// Inner nodes
					else {

						ArrayList<String> leftJrl = new ArrayList<String>();
						ArrayList<String> rightJrl = new ArrayList<String >();
						ArrayList<String> jrl = parentTemp.getJournals();

						ArrayList<ScholarNode> leftCh = new ArrayList<ScholarNode>();
						ArrayList<ScholarNode> rightCh = new ArrayList<ScholarNode>();
						ArrayList<ScholarNode> perCh = parentTemp.getAllChildren();

						parentTemp = (ScholarNodeSecondaryIndex) parentTemp.getParent();

						index = 0;
						while (index < ScholarNode.order) {
							leftJrl.add(jrl.get(index));
							leftCh.add(perCh.get(index));
							index++;
						}
						leftCh.add(perCh.get(index));
						String rn = jrl.get(index);
						index++;
						while (index < jrl.size()) {
							rightJrl.add(jrl.get(index));
							rightCh.add(perCh.get(index));
							index++;
						}
						rightCh.add(perCh.get(index));

						ScholarNodeSecondaryIndex leftNd = new ScholarNodeSecondaryIndex(parentTemp, leftJrl, leftCh);
						ScholarNodeSecondaryIndex rightNd = new ScholarNodeSecondaryIndex(parentTemp, rightJrl, rightCh);

						for (ScholarNode s : leftNd.getAllChildren()) {
							s.setParent(leftNd);
						}

						for (ScholarNode s : rightNd.getAllChildren()) {
							s.setParent(rightNd);
						}

						String tt = jrl.get(ScholarNode.order);
						jrl = parentTemp.getJournals();

						index = 0;
						while (index < jrl.size() && tt.compareTo(jrl.get(index)) >= 0) {
							index++;
						}

						parentTemp.addChild(index, leftNd);
						parentTemp.getAllChildren().set((index + 1), rightNd);
						parentTemp.addJournal(index, rn);
					}
				}

			}

		}

		// add methods to fill both primary and secondary tree
		return;
	}

	public CengPaper searchPaper(Integer paperId) {
		// TODO: Implement this method
		// find the paper with the searched paperId in primary B+ tree
		ScholarNode temp = primaryRoot;

		StringBuilder output = new StringBuilder();
		StringBuilder indent = new StringBuilder();
		while (temp.getType() == ScholarNodeType.Internal) {
			Integer index = 0;

			output.append(indent);
			output.append("<index>\n");

			ScholarNodePrimaryIndex tt = (ScholarNodePrimaryIndex) temp;
			for (Integer s : tt.getPaperIds()){
				if (paperId >= s) index++;
				output.append(indent);
				output.append(s);
				output.append("\n");
			}

			output.append(indent);
			output.append("</index>\n");

			temp = tt.getChildrenAt(index);
			indent.append("\t");
		}

		ScholarNodePrimaryLeaf leaf = (ScholarNodePrimaryLeaf) temp;

		boolean found = false;
		CengPaper paper = null;
		for (CengPaper p : leaf.getPapers()){
			if (p.paperId() == paperId) { found = true; paper = p; break; }
		}

		if (found) {

			output.append(indent);
			output.append("<data>\n");
			output.append(indent);
			output.append("<record>").append(paperId).append("|")
					.append(paper.journal()).append("|")
					.append(paper.paperName()).append("|")
					.append(paper.author()).append("</record>\n");
			output.append(indent);
			output.append("</data>\n");

			System.out.print(output.toString());

			return paper;
		}

		output.append("Could not find ").append(paperId).append("\n");
		System.out.print(output.toString());
		// return value will not be tested, just print according to the specifications
		return null;
	}

	public void searchJournal(String journal) {
		// TODO: Implement this method

		ScholarNode temp = secondaryRoot;

		StringBuilder output = new StringBuilder();
		StringBuilder indent = new StringBuilder();

		while (temp.getType() == ScholarNodeType.Internal) {
			Integer index = 0;

			output.append(indent);
			output.append("<index>\n");

			ScholarNodeSecondaryIndex tt = (ScholarNodeSecondaryIndex) temp;
			for (String s : tt.getJournals()) {
				if (journal.compareTo(s) >= 0) index++;
				output.append(indent);
				output.append(s);
				output.append("\n");
			}

			output.append(indent);
			output.append("</index>\n");

			temp = tt.getChildrenAt(index);
			indent.append("\t");
		}

		ScholarNodeSecondaryLeaf leaf = (ScholarNodeSecondaryLeaf) temp;

		boolean found = false;

		ArrayList<Integer> ids = new ArrayList<>();
		Integer index = 0;
		for (String s : leaf.getJournals()){
			if (s.equals(journal)) {
				found = true;
				ids = leaf.getPaperIdBucket().get(index);
				break;
			}
			index++;
		}

		if (found) {
			output.append(indent);
			output.append("<data>\n");
			output.append(indent);
			output.append(journal).append("\n");

			for (Integer s : ids){
				CengPaper paper = findPaperById(s);
				output.append(indent).append("\t");
				output.append("<record>").append(paper.paperId()).append("|")
						.append(paper.journal()).append("|")
						.append(paper.paperName()).append("|")
						.append(paper.author()).append("</record>\n");
			}

			output.append(indent);
			output.append("</data>\n");

			System.out.print(output.toString());
			return;
		}

		output.append("Could not find ").append(journal).append("\n");
		System.out.print(output.toString());

		// find the journal with the searched journal in secondary B+ tree
		return;
	}

	public void printPrimaryScholar() {
		// TODO: Implement this method
		StringBuilder output = new StringBuilder();
		Integer indent = 0;
		printPrHelper(primaryRoot, indent, output);

		System.out.print(output.toString());
		// print the primary B+ tree in Depth-first order
		return;
	}

	public void printSecondaryScholar() {
		// TODO: Implement this method
		StringBuilder output = new StringBuilder();
		Integer indent = 0;
		printScHelper(secondaryRoot, indent, output);

		System.out.print(output.toString());
		// print the secondary B+ tree in Depth-first order
		return;
	}

	// Extra functions if needed
	public CengPaper findPaperById(int paperId) {
		ScholarNode temp = primaryRoot;

		while (temp.getType() == ScholarNodeType.Internal) {
			Integer index = 0;

			ScholarNodePrimaryIndex internalNode = (ScholarNodePrimaryIndex) temp;
			for (Integer id : internalNode.getPaperIds()) {
				if (paperId >= id) index++;
				else break;
			}

			temp = internalNode.getChildrenAt(index);
		}

		ScholarNodePrimaryLeaf leaf = (ScholarNodePrimaryLeaf) temp;

		for (CengPaper paper : leaf.getPapers()) {
			if (paper.paperId() == paperId) {
				return paper;
			}
		}

		return null;
	}

	public void printPrHelper(ScholarNode node, Integer indent, StringBuilder output) {
		Integer i = 0;
		StringBuilder ind = new StringBuilder();
		while (i < indent) {
			ind.append("\t");
			i++;
		}

		if (node.getType() == ScholarNodeType.Leaf) {
			ScholarNodePrimaryLeaf leaf = (ScholarNodePrimaryLeaf) node;

			output.append(ind);
			output.append("<data>\n");
			for(CengPaper p : leaf.getPapers()){
				output.append(ind);
				output.append("<record>").append(p.paperId()).append("|")
						.append(p.journal()).append("|")
						.append(p.paperName()).append("|")
						.append(p.author()).append("</record>\n");
			}
			output.append(ind);
			output.append("</data>\n");
		}

		else {
			ScholarNodePrimaryIndex internalNode = (ScholarNodePrimaryIndex) node;

			output.append(ind);
			output.append("<index>\n");
			for (Integer in : internalNode.getPaperIds()){
				output.append(ind);
				output.append(in);
				output.append("\n");
			}
			output.append(ind);
			output.append("</index>\n");

			for (ScholarNode n : internalNode.getAllChildren()){
				printPrHelper(n, indent + 1, output);
			}

		}

		return;
	}

	public void printScHelper(ScholarNode node, Integer indent, StringBuilder output) {
		Integer i = 0;
		StringBuilder ind = new StringBuilder();
		while (i < indent) {
			ind.append("\t");
			i++;
		}

		if (node.getType() == ScholarNodeType.Leaf) {
			ScholarNodeSecondaryLeaf leaf = (ScholarNodeSecondaryLeaf) node;
			output.append(ind);
			output.append("<data>\n");
			i = 0;
			for (String s : leaf.getJournals()) {
				output.append(ind);
				output.append(s);
				output.append("\n");
				for (Integer in : leaf.papersAtIndex(i)){
					output.append(ind).append("\t");
					output.append("<record>").append(in).append("</record>\n");
				}
				i++;
			}
			output.append(ind);
			output.append("</data>\n");
		}

		else {
			ScholarNodeSecondaryIndex internalNode = (ScholarNodeSecondaryIndex) node;

			output.append(ind);
			output.append("<index>\n");

			for (String s : internalNode.getJournals()) {
				output.append(ind);
				output.append(s);
				output.append("\n");
			}
			output.append(ind);
			output.append("</index>\n");
			for (ScholarNode n : internalNode.getAllChildren()){
				printScHelper(n, indent + 1, output);
			}

			return;
		}

	}
}



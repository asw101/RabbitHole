/*******************************************************************************
 * Copyright (c) 2006, 2015, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package org.alice.ide.croquet.models.project.find.croquet.tree;

import org.alice.ide.croquet.models.project.find.core.SearchResult;
import org.alice.ide.croquet.models.project.find.croquet.AbstractFindComposite;
import org.alice.ide.croquet.models.project.find.croquet.tree.nodes.DeclarationSeachTreeNode;
import org.alice.ide.croquet.models.project.find.croquet.tree.nodes.ExpressionSearchTreeNode;
import org.alice.ide.croquet.models.project.find.croquet.tree.nodes.SearchTreeNode;
import org.lgna.croquet.CustomSingleSelectTreeState;
import org.lgna.croquet.ItemCodec;
import org.lgna.croquet.codecs.DefaultItemCodec;
import org.lgna.project.ast.Expression;

import javax.swing.Icon;
import java.util.UUID;

/**
 * @author Matt May
 */
public class FindReferencesTreeState extends CustomSingleSelectTreeState<SearchTreeNode> {

  private static final SearchTreeNode root = new SearchTreeNode(null);
  private static final ItemCodec<SearchTreeNode> SEARCH_TREE_NODE_CODEC = DefaultItemCodec.createInstance(SearchTreeNode.class);

  public FindReferencesTreeState() {
    super(AbstractFindComposite.FIND_COMPOSITE_GROUP, UUID.fromString("88fc8668-1de6-4976-9f3b-5c9688675e2b"), root, SEARCH_TREE_NODE_CODEC);
  }

  @Override
  protected String getTextForNode(SearchTreeNode node) {
    return "TODO: get text";
  }

  @Override
  protected Icon getIconForNode(SearchTreeNode node) {
    return null;
  }

  @Override
  public SearchTreeNode getParent(SearchTreeNode node) {
    return node.getParent();
  }

  @Override
  protected int getChildCount(SearchTreeNode parent) {
    return parent.getChildren().size();
  }

  @Override
  protected SearchTreeNode getChild(SearchTreeNode parent, int index) {
    return parent.getChildren().get(index);
  }

  @Override
  protected int getIndexOfChild(SearchTreeNode parent, SearchTreeNode child) {
    return parent.getChildren().indexOf(child);
  }

  @Override
  protected SearchTreeNode getRoot() {
    return root;
  }

  @Override
  public boolean isLeaf(SearchTreeNode node) {
    return node.getIsLeaf();
  }

  public void refreshWith(SearchResult searchObject) {
    this.setValueTransactionlessly(null);
    root.removeAllChildren();
    for (FindReferencesTreeStateLogic.ReferenceGroup group : FindReferencesTreeStateLogic.groupReferences(searchObject)) {
      SearchTreeNode declarationNode = new DeclarationSeachTreeNode(root, group.getDeclaration());
      root.addChild(declarationNode);
      for (Expression reference : group.getReferences()) {
        declarationNode.addChild(new ExpressionSearchTreeNode(declarationNode, reference));
      }
    }
    refreshAll();
  }

  //  public List<Expression> getReferencesForSearchResult( SearchResult searchObject ) {
  //    List<Expression> rv = Lists.newArrayList();
  //    for( Expression reference : searchObject.getReferences() ) {
  //      UserMethod userMethod = reference.getFirstAncestorAssignableTo( UserMethod.class );
  //      if( showGenerated || !userMethod.getManagementLevel().isGenerated() ) {
  //        rv.add( reference );
  //      }
  //    }
  //    searchObject.setFilteredReferences( rv );
  //    return rv;
  //  }

  public void moveSelectedUpOne() {
    this.setValueTransactionlessly(FindReferencesTreeStateLogic.moveSelectedUpOne(root, this.getValue()));
  }

  public void moveSelectedDownOne() {
    this.setValueTransactionlessly(FindReferencesTreeStateLogic.moveSelectedDownOne(root, this.getValue()));
  }

  public SearchTreeNode selectAtCoordinates(int a, int b) {
    return FindReferencesTreeStateLogic.selectAtCoordinates(root, a, b);
  }

  public boolean isEmpty() {
    return FindReferencesTreeStateLogic.hasRows(root);
  }

  public SearchTreeNode getTopValue() {
    return FindReferencesTreeStateLogic.getTopValue(root);
  }

  public TwoDimensionalTreeCoordinate getSelectedCoordinates() {
    FindReferencesTreeStateLogic.TreeCoordinate coordinate = FindReferencesTreeStateLogic.getSelectedCoordinates(root, this.getValue());
    return new TwoDimensionalTreeCoordinate(coordinate.getA(), coordinate.getB());
  }

  public class TwoDimensionalTreeCoordinate {
    private final int a;
    private final int b;

    public TwoDimensionalTreeCoordinate(int a, int b) {
      this.a = a;
      this.b = b;
    }

    public int getA() {
      return a;
    }

    public int getB() {
      return b;
    }
  }
}

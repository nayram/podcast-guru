package ba.codingstoic


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import ba.codingstoic.player.PlayerViewModel
import ba.codingstoic.podcast.PodcastItem
import ba.codingstoic.podcast.PodcastSectionHeader
import ba.codingstoic.podcast.PodcastsViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment() {
    private val podcastsViewModel: PodcastsViewModel by viewModel()
    private val playerViewModel: PlayerViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newAdapter = GroupAdapter<GroupieViewHolder>()
        val hotRightNowSection = Section()
        hotRightNowSection.setHeader(PodcastSectionHeader("Hot"))
        val newest = Section()
        newest.setHeader(PodcastSectionHeader("Newest"))

        newAdapter.add(hotRightNowSection)
        newAdapter.add(newest)

        newAdapter.setOnItemClickListener { item, _ ->
            if (item is PodcastItem) {
                playerViewModel.play(item.podcast.episodes)
            }
        }

        podcastsViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            podcast_rv.visibility = if (it) View.INVISIBLE else View.VISIBLE
            loading_indicator.visibility = if (it) View.VISIBLE else View.GONE
        })

        podcastsViewModel.podcasts.observe(viewLifecycleOwner, Observer { list ->
            hotRightNowSection.clear()
            hotRightNowSection.addAll(list.hot.map { PodcastItem(it) })
            newest.clear()
            newest.addAll(list.newest.map { PodcastItem(it) })
        })

        podcast_rv.layoutManager = LinearLayoutManager(context)
        podcast_rv.adapter = newAdapter

        podcastsViewModel.getPodcasts()
    }
}

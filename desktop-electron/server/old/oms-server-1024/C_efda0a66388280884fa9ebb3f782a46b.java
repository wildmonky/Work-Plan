import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_efda0a66388280884fa9ebb3f782a46b
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return "<span datavalue='"+mapRow.get("BZ")+"' onclick='pw_notify_deadnumline_view(this);'>"+mapRow.get("BZ")+"</span>";

    }
}
